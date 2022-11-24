#!/bin/bash

docker rm prometheus-exp

docker build . -t prometheus-exp

docker volume create prometheus-exp-volume

#9090 port we have
docker run --log-driver=fluentd --log-opt tag="docker.{{.ID}}" --log-opt labels="instance-id" \
  --label "instance-id=prometheus-0" \
  --network host -v "prometheus-exp-volume:/prometheus" --name prometheus-exp prometheus-exp