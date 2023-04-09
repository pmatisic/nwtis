#!/bin/bash
NETWORK=pmatisic_mreza_1
docker run -it -d \
-p 8090:8080 \
--network=$NETWORK \
--ip 200.20.0.2 \
--name=pmatisic_tomcat \
--hostname=pmatisic_tomcat \
pmatisic_tomcat:10.1.7 &
wait
