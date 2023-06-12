#!/bin/bash

echo "Gasim docker..."
docker stop pmatisic_payara_micro
echo "Brišem kontejner..."
docker rm pmatisic_payara_micro
echo "Pripremam sliku..."
./scripts/pripremiSliku.sh
echo "Pokrećem sliku..."
./scripts/pokreniSliku.sh
