#!/bin/bash
NETWORK=pmatisic_mreza_1

docker run -it -d \
  -p 8070:8080 \
  --network=$NETWORK \
  --ip 200.20.0.4 \
  --name=pmatisic_payara_micro \
  --hostname=pmatisic_payara_micro \
  pmatisic_payara_micro:6.2023.4 \
  --deploy /opt/payara/deployments/pmatisic_zadaca_2_wa_1-1.0.0.war \
  --contextroot pmatisic_zadaca_2_wa_1 \
  --noCluster &

wait
