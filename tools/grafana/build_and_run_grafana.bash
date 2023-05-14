#!/bin/bash
name="system-template-grafana"

docker stop $name || true

docker rm $name || true

docker build . -t $name

# docker volume create grafana-exp-volume
# docker run --network host -v "grafana-exp-volume:/var/lib/grafana" --name grafana-exp grafana-exp

docker run -d --network host --name $name $name