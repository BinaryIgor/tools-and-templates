#!/bin/bash

docker rm prometheus-exp

docker build . -t prometheus-exp

docker volume create prometheus-exp-volume

#9090 port we have
docker run --network host -v "prometheus-exp-volume:/prometheus" --name prometheus-exp prometheus-exp