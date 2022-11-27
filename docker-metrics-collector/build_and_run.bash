#!/bin/bash

docker build . -t docker-metrics-collector

docker rm docker-metrics-collector

docker run -v /var/run/docker.sock:/var/run/docker.sock --name docker-metrics-collector docker-metrics-collector