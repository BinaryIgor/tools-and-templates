#!/bin/bash

docker rm test-app

docker build . -t test-app

#9090 port we have
docker run --log-driver=fluentd --log-opt tag="docker.{{.ID}}" --name test-app test-app