#!/bin/bash

NETWORK=pmatisic_mreza_1

docker run -d \
  -p 9001:9001 \
  --network=$NETWORK \
  --ip 200.20.0.3 \
  --name=nwtishsqldb_4 \
  --hostname=nwtishsqldb_4 \
  --mount type=bind,source=/opt/hsqldb-2.7.1/hsqldb/data,target=/opt/data \
  nwtishsqldb_4:1.0.0 &

wait
