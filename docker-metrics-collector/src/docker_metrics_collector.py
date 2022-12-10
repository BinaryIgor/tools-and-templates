from datetime import datetime
import json
import logging
import random
import signal
import sys
import time
from os import environ

import docker
from docker.errors import NotFound
import requests

logging.basicConfig(level=logging.INFO,
                    format="%(asctime)s.%(msecs)03d;%(levelname)s;%(message)s",
                    datefmt="%Y-%m-%d %H:%M:%S")
LOG = logging.getLogger(__file__)

CONTAINER_ID_FIELD = "containerId"
CONTAINER_NAME_FIELD = "containerName"

INSTANCE_ID_FIELD = "instanceId"
DEFAULT_INSTANCE_ID_SUFFIX = "default"
INSTANCE_ID_LABEL = environ.get("INSTANCE_ID_LABEL", "")

MACHINE_NAME = environ.get("MACHINE_NAME", "anonymous-machine")

CONSOLE_METRICS_TARGET = "CONSOLE_METRICS_TARGET"
METRICS_TARGET_URL = environ.get('METRICS_TARGET_URL', CONSOLE_METRICS_TARGET)
METRICS_TARGET_HEADERS = environ.get("METRICS_TARGET_HEADERS", {})
if METRICS_TARGET_HEADERS:
    METRICS_TARGET_HEADERS = {h.split("=")[0].strip(): h.split(
        "=")[1].strip() for h in METRICS_TARGET_HEADERS.split(",")}

COLLECTION_INTERVAL = int(environ.get("COLLECTION_INTERVAL", 10))
LAST_DATA_READ_AT_FILE_PATH = environ.get("LAST_DATA_READ_AT_FILE",
                                          "/tmp/docker-metrics-collector-last-data-read-at.txt")


class DockerContainers:
    """
    Wrapper class for containers states.
    We need previous_containers field to make sure that we collect logs from containers that died after,
    but before the next logs collection.
    """

    def __init__(self, docker_client):
        self.previous_containers = []
        self.client = docker_client

    def get(self):
        def instance_id_label(container):
            labels = container['Labels']
            instance_id = labels.get(INSTANCE_ID_LABEL, None)
            if not instance_id:
                instance_id = f'{container_name(container)}-{DEFAULT_INSTANCE_ID_SUFFIX}'
            return instance_id

        def container_name(container):
            return container["Names"][0].replace("/", "")

        fetched = [{CONTAINER_ID_FIELD: c['Id'],
                    CONTAINER_NAME_FIELD: container_name(c),
                    INSTANCE_ID_FIELD: instance_id_label(c)}
                   for c in self.client.containers()]

        all_containers = []
        all_containers.extend(fetched)

        for pc in self.previous_containers:
            if pc not in all_containers:
                all_containers.append(pc)

        self.previous_containers = fetched

        return all_containers


class GracefulShutdown:
    stop = False

    def __init__(self):
        signal.signal(signal.SIGINT, self.exit_gracefully)
        signal.signal(signal.SIGTERM, self.exit_gracefully)

    # Args are needed due to signal handler specification
    def exit_gracefully(self, *args):
        self.stop = True


SHUTDOWN = GracefulShutdown()


def connected_docker_client_retrying():
    def new_client():
        return docker.APIClient(base_url='unix://var/run/docker.sock')

    LOG.info(f"Starting monitoring of machine {MACHINE_NAME}")
    while True:
        try:
            LOG.info("Trying to get client...")
            client = new_client()
            ver = data_object_formatted(client.version())
            LOG.info(f"Client connected, docker ver: {ver}")
            return client
        except Exception:
            if SHUTDOWN.stop:
                LOG.info("Shutdown requested, exiting")
                sys.exit(1)

            retry_interval = random_retry_interval()
            log_exception(
                f"Problem while connecting to docker client, retrying in {retry_interval}s...")
            time.sleep(retry_interval)


def data_object_formatted(data_object):
    return json.dumps(data_object, indent=2)


def random_retry_interval():
    return round(random.uniform(1, 5), 3)


def log_exception(message):
    LOG.exception(f"{message}")
    print()


def current_timestamp():
    return int(time.time())


def current_timestamp_millis():
    return int(time.time() * 1000)


DOCKER_CONTAINERS = DockerContainers(connected_docker_client_retrying())


def keep_collecting_and_sending():
    try:
        do_keep_collecting_and_sending()
    except Exception:
        log_exception("Problem while collecting, retrying...")
        keep_collecting_and_sending()


def do_keep_collecting_and_sending():
    while True:
        if SHUTDOWN.stop:
            LOG.info("Shutdown requested, exiting gracefully")
            break

        LOG.info("Checking containers...")
        gather_and_send_metrics()
        print("...")

        if SHUTDOWN.stop:
            LOG.info("Shutdown requested, exiting gracefully")
            break

        print(f"Sleeping for {COLLECTION_INTERVAL}s")
        print()

        time.sleep(COLLECTION_INTERVAL)


def gather_and_send_metrics():
    running_containers = DOCKER_CONTAINERS.get()

    last_data_read_at = current_timestamp()

    LOG.info(f"Have {len(running_containers)} running containers, checking their metrics/stats...")

    c_metrics = containers_metrics(running_containers)

    print()
    LOG.info("Metrics checked.")
    print()
    send_metrics_if_present(c_metrics)

    print()

    update_last_data_read_at_file(last_data_read_at)


def send_metrics_if_present(c_metrics):
    if c_metrics:
        try:
            LOG.info(f"Sending metrics of {len(c_metrics)} containers...")

            metrics_object = {
                'source': MACHINE_NAME,
                'metrics': c_metrics
            }

            if METRICS_TARGET_URL == CONSOLE_METRICS_TARGET:
                LOG.info("Console metrics target...")
                print(data_object_formatted(metrics_object))
                print()
            else:
                send_metrics(metrics_object)

            LOG.info("Metrics sent")
        except Exception:
            log_exception("Failed to send metrics..")
    else:
        LOG.info("No metrics to send")


def send_metrics(containers_metrics, retries=3):
    for i in range(1 + retries):
        try:
            if METRICS_TARGET_HEADERS:
                r = requests.post(
                    METRICS_TARGET_URL, json=containers_metrics, headers=METRICS_TARGET_HEADERS)
            else:
                r = requests.post(METRICS_TARGET_URL, json=containers_metrics)

            r.raise_for_status()

            return
        except Exception:
            if i < retries:
                retry_interval = random_retry_interval()
                LOG.info(
                    f"Fail to send metrics, will retry in {retry_interval}s")
                time.sleep(retry_interval)
            else:
                raise


def containers_metrics(containers):
    containers_metrics = []

    for c in containers:
        c_id = c[CONTAINER_ID_FIELD]
        c_name = c[CONTAINER_NAME_FIELD]
        i_id = c[INSTANCE_ID_FIELD]

        print()
        LOG.info(f"Checking {c_name}:{c_id} container metrics...")

        c_metrics = fetched_container_metrics(container_id=c_id,
                                              container_name=c_name,
                                              instance_id=i_id)

        if c_metrics:
            containers_metrics.append(c_metrics)
            print()

        LOG.info(f"{c_name}:{c_id} container metrics checked")

    return containers_metrics


def fetched_container_metrics(container_id, container_name, instance_id):
    try:
        LOG.info("Gathering metrics...")

        c_metrics = DOCKER_CONTAINERS.client.stats(container_id, stream=False)

        memory_metrics = c_metrics['memory_stats']
        prev_cpu_metrics = c_metrics['precpu_stats']
        cpu_metrics = c_metrics['cpu_stats']

        inspection_result = DOCKER_CONTAINERS.client.inspect_container(container_id)
        started_at = inspection_result['State'].get('StartedAt', datetime.utcnow().isoformat(sep="T"))

        metrics_object = formatted_container_metrics(name=container_name,
                                                     instance_id=instance_id,
                                                     started_at=started_at,
                                                     memory_metrics=memory_metrics,
                                                     cpu_metrics=cpu_metrics,
                                                     precpu_metrics=prev_cpu_metrics)

        LOG.info("Metrics gathered")

        return metrics_object
    except NotFound:
        LOG.info(f"Container {container_id}:{container_id} not found, skipping!")
        print()
        return None
    except Exception:
        log_exception("Failed to gather metrics")
        return None


def formatted_container_metrics(name, instance_id, started_at, memory_metrics, cpu_metrics, precpu_metrics):
    try:
        return {
            'containerName': name,
            'instanceId': instance_id,
            'startedAt': started_at,
            'timestamp': current_timestamp_millis(),
            'usedMemory': memory_metrics['usage'],
            'maxMemory': memory_metrics['limit'],
            'cpuUsage': container_cpu_metrics(cpu_metrics, precpu_metrics)
        }
    except KeyError:
        # We get this, when docker_client.stats() return empty metrics for killed container. We don't care about that
        return None


def container_cpu_metrics(cpu_metrics, precpu_metrics):
    prev_usage = precpu_metrics['cpu_usage']
    prev_container_usage = prev_usage['total_usage']
    prev_system_usage = precpu_metrics['system_cpu_usage']

    current_usage = cpu_metrics['cpu_usage']
    current_container_usage = current_usage['total_usage']
    current_system_usage = cpu_metrics['system_cpu_usage']

    cpu_delta = current_container_usage - prev_container_usage
    system_delta = current_system_usage - prev_system_usage

    if cpu_delta > 0 and system_delta > 0:
        percpu_usages = current_usage.get('percpu_usage')
        cores_num = len(percpu_usages) if percpu_usages else 1
        cpu_usage = (cpu_delta / system_delta) * cores_num
        # Value is in the range of 0 - 1, so multiplying it by 100 we need only up to 2 digits precision like 12.34%
        return round(cpu_usage, 4)

    return 0


def update_last_data_read_at_file(read_at):
    try:
        LOG.info(
            f"Updating last-data-read-at file: {LAST_DATA_READ_AT_FILE_PATH}")

        with open(LAST_DATA_READ_AT_FILE_PATH, "w") as f:
            f.write(str(read_at))

        LOG.info("File updated")
        print()
    except Exception:
        log_exception("Problem while updating last data read at file...")


keep_collecting_and_sending()
