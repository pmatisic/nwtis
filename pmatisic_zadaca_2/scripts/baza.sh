#!/bin/bash

# Provjeravam postoji li mreža pmatisic_mreza_1
mreza_postoji=$(docker network ls | grep -w pmatisic_mreza_1)

# Ako mreža ne postoji, pokreni skriptu pripremiMrezu.sh
if [ -z "$mreza_postoji" ]; then
    ./scripts/pripremiMrezu.sh
fi

# Provjeravam postoji li spremnik s imenom nwtishsqldb_4
spremnik_postoji=$(docker ps -a --filter name=nwtishsqldb_4 --format "{{.Names}}")

# Ako spremnik postoji, ukloni ga
if [ ! -z "$spremnik_postoji" ]; then
    echo "Spremnik nwtishsqldb_4 već postoji, uklanjam..."
    docker rm -f nwtishsqldb_4
fi

# Pokreni ostale skripte
echo "Pripremam sliku..."
./scripts/pripremiSliku.sh
echo "Pokrećem sliku..."
./scripts/pokreniSliku.sh
