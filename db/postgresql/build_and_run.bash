#!/bin/bash

docker rm postgres-exp

docker build . -t postgres-exp

docker run -p "5555:5432" \
  -v "$HOME/postgres-exp-volume:/var/lib/postgresql/data" \
  --name postgres-exp postgres-exp