#!/bin/bash

echo "GASI DOCKER"
docker stop pmatisic_payara_micro
echo "BRISI CONTAINER"
docker rm pmatisic_payara_micro
echo "PRIPREMI SLIKU"
./scripts/pripremiSliku.sh
echo "POKRENI SLIKU"
./scripts/pokreniSliku.sh
