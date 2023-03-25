#!/bin/bash

fail_deployment() {
  echo "App is not running/healthy, renaming ${app_backup} back to ${app}..."
  docker rename ${app_backup} ${app}
  echo "App renamed, try deploying again!"
  exit 1
}

found_container=$(docker ps -q -f name="${app}")
app_backup="${app}-backup"

if [ "$found_container" ]; then
  echo "Renaming current ${app} container to ${app_backup}..."
  docker rm ${app_backup}
  docker rename ${app} ${app_backup}
fi

echo "Removing previous container, if wasn't running..."
docker rm ${app}

echo
echo "Starting new ${app} version..."
echo

${pre_run_cmd}
${run_cmd}
${post_run_cmd}

echo
echo "App started, will check if it is running after 5s..."
sleep 5

status=$(docker container inspect -f '{{.State.Status}}' ${app})
if [ ${status} == 'running' ]; then
  echo "App is running, checking its health-check..."
  #TODO: check http code (if needed)
  curl --retry-connrefused --retry 10 --retry-delay 1 --fail ${app_health_check_url}
  echo
  if [ $? == 0 ]; then
    echo "App is healthy!"
  else
    fail_deployment
  fi
else
  fail_deployment
fi

cwd=$PWD
cd ${upstream_nginx_dir}
bash "update_app_url.bash" ${app_url}
cd ${cwd}

echo
echo "Nginx updated and running with new app, cleaning previous after a few seconds!"
sleep 5
echo

if [ "$found_container" ]; then
  echo "Stopping previous ${app_backup} container..."
  docker stop ${app_backup} --time ${stop_timeout}
fi

echo "Removing previous container...."
docker rm ${app_backup}