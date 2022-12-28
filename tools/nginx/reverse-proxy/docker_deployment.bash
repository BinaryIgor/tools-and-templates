#!/bin/bash

# Zero downtime deployment with docker application + nginx
# Maintainer: heitkotter [at] gmail.com

# Requirements
## Image on production server
## Nginx cannot be a container. This guarantees that containers will be served after reboot

# Script steps
## Remove previously backed up container
## Rename container for backup
## Create container with new image (expose to random port)
## Wait and check connection with curl or similar
## Generate upstream config file with new port
## Copy contents of public folder to nginx expected static root
## Reload nginx configuration
## Stop backup container

# Usage: $0 [tag=latest]


CONTAINER=myapplication
CONTAINER_OPTS="--link mongo:mongo" # Optional
CONTAINER_EXPOSED_PORT=8080
IMAGE=user/myimage
IMAGE_STATIC_FILES=/opt/myapplication/public
NGINX_STATIC_ROOT=/opt/myapplication
NGINX_UPSTREAM_NAME=myapplication
NGINX_CONFD_DIR=/etc/nginx/conf.d
TAG=${1:-latest}


echo "Deploying $IMAGE:$TAG as $CONTAINER"


randomPort() {
    RANDOM_PORT=$(awk 'BEGIN{srand();print int(rand()*30000)+30000 }') # Between 30000 and 60000
    while netstat -atwn | grep "^.*:${RANDOM_PORT}.*:\*\s*LISTEN\s*$"
    do
    RANDOM_PORT=$(( ${RANDOM_PORT} + 1 ))
    done
    echo $RANDOM_PORT
}


RUNNING=$(docker inspect --format="{{ .State.Running }}" $CONTAINER 2> /dev/null)
if [ -z "$RUNNING" ]; then
    echo "Container $CONTAINER does not exist and will be created"
elif [ "$RUNNING" == "false" ]; then
    docker rm -fv $CONTAINER 2> /dev/null
    echo "Container was not running, thus was removed"
else
    docker rm -fv $CONTAINER-backup 2> /dev/null
    docker rename $CONTAINER $CONTAINER-backup 2> /dev/null
    echo "Created backup container $CONTAINER-backup"
fi

docker run $CONTAINER_OPTS --name $CONTAINER --restart=always \
    -d -p $(randomPort):$CONTAINER_EXPOSED_PORT $IMAGE:$TAG || exit 1
echo "Started container $CONTAINER"

ADDRESS=$(docker port $CONTAINER $CONTAINER_EXPOSED_PORT 2> /dev/null)
if [ -z "$ADDRESS" ]; then
    >&2 echo "ERROR: No port exposed!"
    exit 1
fi

sleep 5

STATUS_CODE=$(curl -s -o /dev/null -w "%{http_code}" $ADDRESS)
if [ "$STATUS_CODE" == "000" ]; then
    >&2 echo "ERROR: Server is not running!"
    exit 1
fi

echo "upstream $NGINX_UPSTREAM_NAME {${nl}
    server $(docker port $CONTAINER $CONTAINER_EXPOSED_PORT);${nl}
}" > ${NGINX_CONFD_DIR}/${NGINX_UPSTREAM_NAME}_upsteam.conf || exit 1
echo "Configured upstream $NGINX_UPSTREAM_NAME"

docker cp $CONTAINER:$IMAGE_STATIC_FILES $NGINX_STATIC_ROOT || exit 1
echo "Copied static files to $NGINX_STATIC_ROOT"

nginx -s reload || exit 1
echo "Reloaded nginx"

docker kill $CONTAINER-backup 2> /dev/null
echo "Stopped $CONTAINER-backup"