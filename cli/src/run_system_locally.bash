#!/bin/bash
set -e

is_container_running() {
  container_state=$(docker container inspect -f '{{.State.Status}}' "$1")
  if [[ "$container_state" == "running" ]] ; then
    func_result=1
  else
    func_result=0
  fi
}

validate_all_containers_are_running() {
    all_running=1
    for c in "${COMPONENTS[@]}"; do
       echo "Checking $c..."
       is_container_running "$c"
       if [[ $func_result == 0 ]]; then
         all_running=0
         echo "$c is not running!"
       fi
       sleep 1
     done

     if [[ $all_running == 0 ]] ; then
       echo
       echo "Some containers are not running, exiting"
       echo
       stop_all_containers
       exit 1
    fi

    if [[ $db_migration_code != 0 ]]; then
      echo
      echo "Migration of db failed, exiting"
      echo
      stop_all_containers
      exit 1
    fi
}

stop_all_containers() {
  for c in "${COMPONENTS_REVERSED[@]}"; do
    echo "Stopping $c..."
    docker stop "$c"
  done
}

wait_for_container() {
  WAIT_INTERVAL=${2:-$WAIT_FOR_CONTAINER_INTERVAL}
  echo "Waiting for $1 to be up and running..."

  while true; do
      sleep "$WAIT_INTERVAL"
      is_container_running "$1"
      if [ $func_result = 1 ]; then
          break
      fi
      echo "$1 is still not running, waiting..."
      echo "..."
      echo "You can check out its logs by running: docker logs $1 --follow"
  done
}

if [ -z "$SYSTEM_DATA_PATH" ]; then
  DEFAULT_SYSTEM_DATA_PATH="$HOME/system-data"
  echo "SYSTEM_DATA_PATH env variable is not set, default to ${DEFAULT_SYSTEM_DATA_PATH}"
  export SYSTEM_DATA_PATH=$DEFAULT_SYSTEM_DATA_PATH
fi

export PG_DB_VOLUME_PATH=$SYSTEM_DATA_PATH/pg-db-volume
export FILES_VOLUME_PATH=$SYSTEM_DATA_PATH/files-volume
export LOGS_VOLUME_PATH=$SYSTEM_DATA_PATH/logs-volume

export STATIC_FILES_PATH=${STATIC_FILES_PATH:-$PWD}

export CERTS_VOLUME_PATH="${PWD}/../config/fake-certs"
export SECRETS_VOLUME_PATH="${PWD}/../config/local-secrets"
export PACKAGES_DIR="${PWD}/../target"

echo "Creating volume directories, if they don't exist..."
mkdir -p "$SYSTEM_DATA_PATH"
mkdir -p "$PG_DB_VOLUME_PATH"
mkdir -p "$FILES_VOLUME_PATH"
mkdir -p "$LOGS_VOLUME_PATH"

echo
echo "Volumes created"
echo

WAIT_FOR_CONTAINER_INTERVAL=1

FLUENTD="fluentd"
MONITOR="monitor"
DOCKER_METRICS_COLLECTOR="docker-metrics-collector"

POSTGRES_DB="postgres-db"

NGINX_API_GATEWAY="nginx-api-gateway"
NGINX_SOCKET_SERVER_GATEWAY="nginx-socket-server-gateway"

API="api"
PROCESSOR="processor"
SOCKET_SERVER="socket-server"

COTURN="coturn"

COMPONENTS=("$FLUENTD" "$MONITOR" "$DOCKER_METRICS_COLLECTOR" "$POSTGRES_DB" "$API" "$NGINX_API_GATEWAY" "$PROCESSOR" "$SOCKET_SERVER" "$NGINX_SOCKET_SERVER_GATEWAY" "$COTURN")
COMPONENTS_REVERSED=("$COTURN" "$NGINX_SOCKET_SERVER_GATEWAY" "$SOCKET_SERVER" "$PROCESSOR" "$NGINX_API_GATEWAY" "$API" "$POSTGRES_DB" "$DOCKER_METRICS_COLLECTOR" "$MONITOR" "$FLUENTD")

echo "Building all..."

if [[ $1 == *build ]]; then
  echo "We will build system first"
  skip_commons="false"
  for c in "${COMPONENTS[@]}"; do
    if [ $skip_commons == "true" ]; then
      python build_app.py --env local --app $c --skip_tests --skip_commons --skip_image_export
    else
      python build_app.py --env local --app $c --skip_tests --skip_image_export
    fi

    if [ $c == $API ]; then
      skip_commons="true"
    fi
  done
else
  echo "Skipping build, using prepared packages..."
fi

set +e

trap break INT

echo "Images are ready to use, starting them in the right order"

echo "Starting ${FLUENTD}..."
cd $PACKAGES_DIR
cd $FLUENTD

bash run.bash

wait_for_container $FLUENTD
#echo "..this can take a while and can fail sometimes!"
#sleep 20

echo
echo "$FLUENTD is up!"
echo

echo "Starting ${MONITOR}..."
cd $PACKAGES_DIR
cd $MONITOR

bash run.bash

wait_for_container $MONITOR

echo
echo "$MONITOR is up!"
echo

echo "Starting ${DOCKER_METRICS_COLLECTOR}..."
cd $PACKAGES_DIR
cd $DOCKER_METRICS_COLLECTOR

bash run.bash

wait_for_container $DOCKER_METRICS_COLLECTOR

echo
echo "$DOCKER_METRICS_COLLECTOR is up!"
echo

echo "Starting ${POSTGRES_DB}..."
cd $PACKAGES_DIR
cd $POSTGRES_DB

bash run.bash

wait_for_container $POSTGRES_DB

echo
echo "$POSTGRES_DB is up!"
echo

echo "About to initialize $POSTGRES_DB..."

cd ../../src

python init_db.py --env local

echo "$POSTGRES_DB is initialized, performing migrations..."

if [ -z "$DB_MIGRATION_REPAIR" ]; then
  python migrate_db.py --env local
else
  python migrate_db.py --env local --repair
fi

db_migration_code=$?
echo
echo "$POSTGRES_DB migrations executed with code: $db_migration_code"
echo

echo "Starting ${API}..."
cd $PACKAGES_DIR
cd $API

bash run.bash

wait_for_container $API 5

echo
echo "$API is up!"
echo

echo "Starting ${NGINX_API_GATEWAY}..."
cd $PACKAGES_DIR
cd $NGINX_API_GATEWAY

bash run.bash

wait_for_container $NGINX_API_GATEWAY

echo
echo "$NGINX_API_GATEWAY is up!"
echo

echo "Starting ${PROCESSOR}..."
cd $PACKAGES_DIR
cd $PROCESSOR

bash run.bash

wait_for_container $PROCESSOR 5

echo
echo "$PROCESSOR is up!"
echo

echo "Starting ${SOCKET_SERVER}..."
cd $PACKAGES_DIR
cd $SOCKET_SERVER

bash run.bash

wait_for_container $SOCKET_SERVER 5

echo
echo "$SOCKET_SERVER is up!"
echo

echo "Starting ${NGINX_SOCKET_SERVER_GATEWAY}..."
cd $PACKAGES_DIR
cd $NGINX_SOCKET_SERVER_GATEWAY

bash run.bash

wait_for_container $NGINX_SOCKET_SERVER_GATEWAY

echo
echo "$NGINX_SOCKET_SERVER_GATEWAY is up!"
echo

echo "Starting ${COTURN}..."
cd $PACKAGES_DIR
cd $COTURN

bash run.bash

wait_for_container $COTURN

echo
echo "$COTURN is up!"
echo

echo "Checking if all system components are healthy..."
echo
sleep 5
validate_all_containers_are_running

echo
echo "System is up and running!"
echo "You can check whether all components are running by typing:"
echo "docker ps"
echo

while true;
do
   echo "Waiting for stop command"
   echo "..."
   sleep 300
   echo
done

trap - INT
sleep 1

echo "Stopping containers..."
stop_all_containers