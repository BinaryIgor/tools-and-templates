#!/bin/bash

docker rm system-monitor-app

docker build . -t system-monitor-app

docker run --log-driver=fluentd --log-opt tag="docker.{{.ID}}" -p "10101:10101" --name system-monitor-app system-monitor-app