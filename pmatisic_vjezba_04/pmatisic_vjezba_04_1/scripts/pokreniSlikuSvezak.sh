#!/bin/bash
docker run -it -d \
--name=pmatisic_vjezba_04_1S \
--mount source=pmatisic_podaci,target=/usr/app/podaci \
pmatisic_vjezba_04_1:1.0.0 &
wait
