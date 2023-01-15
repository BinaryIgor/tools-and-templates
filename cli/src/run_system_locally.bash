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

    if [[ $application_db_migration_failed != 0 ]] || [[ $system_db_migration_failed != 0 ]]; then
      echo
      echo "Migration for application or system postgres failed, exiting"
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
  SYSTEM_DATA_PATH=$DEFAULT_SYSTEM_DATA_PATH
fi

PG_VOLUME_PATH=$SYSTEM_DATA_PATH/pg-db-volume
SYSTEM_PG_VOLUME_PATH=$SYSTEM_DATA_PATH/system-pg-volume
RABBITMQ_VOLUME_PATH=$SYSTEM_DATA_PATH/rabbitmq-volume
HAIRO_DATA_VOLUME_PATH=$SYSTEM_DATA_PATH/hairo-volume
HAIRO_LOGS_VOLUME_PATH=$SYSTEM_DATA_PATH/logs-volume

CERTS_VOLUME_PATH="${PWD}/../../config/fake-certs"
SECRETS_DIR="${PWD}/../../config/local-secrets"
PACKAGES_DIR="${PWD}/../target"

echo "Creating volume directories, if the don't exist..."
mkdir -p "$SYSTEM_DATA_PATH"
mkdir -p "$APPLICATION_PG_VOLUME_PATH"
mkdir -p "$SYSTEM_PG_VOLUME_PATH"
mkdir -p "$HAIRO_DATA_VOLUME_PATH"
mkdir -p "$HAIRO_LOGS_VOLUME_PATH"

echo
echo "Volumes created"
echo

WAIT_FOR_CONTAINER_INTERVAL=1

APPLICATION_DB="application-postgres-db"
SYSTEM_DB="system-postgres-db"
RABBITMQ="rabbitmq"
NGINX="nginx"

MONITOR="monitor"
API="api"
PROCESSOR="processor"
SOCKETS_SERVER="sockets-server"

COMPONENTS=("$RABBITMQ" "$APPLICATION_DB" "$SYSTEM_DB" "$NGINX" "$MONITOR" "$API" "$PROCESSOR" "$SOCKETS_SERVER")
COMPONENTS_REVERSED=("$SOCKETS_SERVER" "$PROCESSOR" "$API" "$MONITOR" "$NGINX" "$SYSTEM_DB" "$APPLICATION_DB" "$RABBITMQ")

echo "Building all..."

if [[ $1 == *build_jar ]]; then
  echo "We will build new jars first"
  python3 build_all.py --env local --skip_image_export --skip_tests
else
  echo "Skipping jar building"
  python3 build_all.py --env local --skip_jar_build --skip_image_export --skip_tests
fi

set +e

trap break INT

echo "Images are ready to use, starting them in the right order"

echo "Starting ${RABBITMQ}..."
cd $PACKAGES_DIR
cd $RABBITMQ

export RABBITMQ_DATA_PATH=$RABBITMQ_VOLUME_PATH
export RABBITMQ_DEFINITIONS_PATH=$PACKAGES_DIR/$RABBITMQ/definitions.json

bash run.bash

wait_for_container $RABBITMQ

echo
echo "$RABBITMQ is up!"
echo

echo "Starting ${APPLICATION_DB}..."
cd $PACKAGES_DIR
cd $APPLICATION_DB

export ADB_DATA_PATH=${PG_VOLUME_PATH}

bash run.bash

wait_for_container $APPLICATION_DB

echo
echo "$APPLICATION_DB is up!"
echo

echo "About to initialize $APPLICATION_DB..."

cd ../../scripts

python3 init_db.py --env local --db_type application --initial_root_password postgres

echo "$APPLICATION_DB is initialized, performing migrations..."

if [ -z "$DB_MIGRATOR_REPAIR" ]; then
  python3 migrate_db.py --env local --db_type application
else
  python3 migrate_db.py --env local --db_type application --repair
fi

application_db_migration_failed=$?
echo
echo "$APPLICATION_DB migrations executed with code: $application_db_migration_failed"
echo

echo "Starting $SYSTEM_DB..."
cd $PACKAGES_DIR
cd $SYSTEM_DB

export SDB_DATA_PATH=${SYSTEM_PG_VOLUME_PATH}

bash run.bash

wait_for_container $SYSTEM_DB

echo
echo "$SYSTEM_DB is up!"
echo

echo "About to initialize $SYSTEM_DB..."

cd ../../scripts

python3 init_db.py --env local --db_type system --initial_root_password postgres

echo "$SYSTEM_DB is initialized, performing migrations..."

if [ -z "$DB_MIGRATOR_REPAIR" ]; then
  python3 migrate_db.py --env local --db_type system
else
  python3 migrate_db.py --env local --db_type system --repair
fi

system_db_migration_failed=$?
echo
echo "$SYSTEM_DB migrations executed with code: $system_db_migration_failed"
echo

echo "Starting ${NGINX}..."
cd $PACKAGES_DIR
cd $NGINX

export NGINX_CONFIG_PATH=$PACKAGES_DIR/$NGINX/nginx.conf
export NGINX_CERTS_PATH=$CERTS_VOLUME_PATH
export NGINX_SITE_PATH=$NGINX_STATIC_CONTENT_PATH

bash run.bash

wait_for_container $NGINX

echo
echo "$NGINX is up!"
echo

echo "Starting ${MONITOR}..."
cd $PACKAGES_DIR
cd $MONITOR

export HM_LOGS_VOLUME_PATH=$HAIRO_LOGS_VOLUME_PATH
export HM_SECRETS_DIR=$SECRETS_DIR

bash run.bash

wait_for_container $MONITOR

echo
echo "$MONITOR is up!"
echo

echo "Starting ${API}..."
cd $PACKAGES_DIR
cd $API

export HA_FILES_VOLUME_PATH=$HAIRO_DATA_VOLUME_PATH
export HA_SECRETS_DIR=$SECRETS_DIR

bash run.bash

wait_for_container $API

echo
echo "$API is up!"
echo

echo "Starting ${PROCESSOR}..."
cd $PACKAGES_DIR
cd $PROCESSOR

export HP_FILES_VOLUME_PATH=$HAIRO_DATA_VOLUME_PATH
export HP_SECRETS_DIR=$SECRETS_DIR

bash run.bash

wait_for_container $PROCESSOR

echo
echo "$PROCESSOR is up!"
echo

echo "Starting ${SOCKETS_SERVER}..."
cd $PACKAGES_DIR
cd $SOCKETS_SERVER

export HSS_CERTS_PATH=$CERTS_VOLUME_PATH
export HSS_SECRETS_DIR=$SECRETS_DIR

bash run.bash

wait_for_container $SOCKETS_SERVER

echo
echo "$SOCKETS_SERVER is up!"
echo

echo "Checking if all system components are healthy..."
echo
sleep 3
validate_all_containers_are_running

echo
echo "Hairo System is up and running!"
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