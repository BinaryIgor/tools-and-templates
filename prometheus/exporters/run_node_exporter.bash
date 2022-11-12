#!/bin/bash

docker rm node-exporter-exp

docker run -p "9100:9100" --name node-exporter-exp prom/node-exporter