#!/bin/bash

docker rm prometheus-exp

docker build . -t prometheus-exp

docker volume create prometheus-volume

#9090 port we have
docker run --network host -v "prometheus-volume:/prometheus" --name prometheus-exp prometheus-exp