#!/bin/bash

docker rm fluentd-exp
docker build . -t fluentd-exp

#docker run -p 24224:24224 -p 24224:24224/udp --name fluentd-exp fluentd-exp
docker run --network host --name fluentd-exp fluentd-exp