import logging
import signal
import time
import random

logging.basicConfig(level=logging.INFO,
                    format="%(asctime)s.%(msecs)03d;%(levelname)s;%(message)s",
                    datefmt="%Y-%m-%d %H:%M:%S")
LOG = logging.getLogger(__file__)

SHUTDOWN = False

def exit_gracefully(self, *args):
    global SHUTDOWN
    LOG.info("Exit request, see ya!")
    SHUTDOWN = True

signal.signal(signal.SIGINT, exit_gracefully)
signal.signal(signal.SIGTERM, exit_gracefully)

while not SHUTDOWN:
    to_sleep = random.randrange(1, 5)
    LOG.info(f"Sleeping random {to_sleep}s, some data: {random.randrange(1, 1_000_000_000)}...")
    time.sleep(to_sleep)