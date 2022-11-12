#!/bin/bash
docker rm grafana-exp

docker build . -t grafana-exp

docker volume create grafana-exp-volume

docker run --network host -v "grafana-exp-volume:/var/lib/grafana" --name grafana-exp grafana-exp