#!/bin/bash

docker rm kibana-exp

# 5601 is public to be used
docker run --network host \
    --name kibana-exp docker.elastic.co/kibana/kibana:8.5.0