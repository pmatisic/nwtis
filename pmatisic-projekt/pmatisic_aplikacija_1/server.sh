#!/bin/bash

# Provjerava da li je proslijeđen jedan argument
if [ $# -ne 1 ]; then
    echo "Korištenje: $0 <ime datoteke> "
    exit 1
fi

filename=$1

# Provjera format imena datoteke
if [[ ! $filename =~ ^[a-zA-Z0-9._-]+(.txt|.xml|.bin|.json|.yaml)$ ]]; then
    echo "Neispravan format imena datoteke. Molimo koristite ispravno ime datoteke (.txt, .xml, .bin, .json, .yaml)."
    exit 1
fi

# Provjerava da li postoji datoteka koja je proslijeđena kao argument
if [ ! -f $filename ]; then
    echo "Datoteka $filename ne postoji."
    exit 1
fi

# Pokreće poslužitelj s datotekom postavki koja je proslijeđena kao argument
java -cp target/pmatisic_aplikacija_1-1.0.0.jar org.foi.nwtis.pmatisic.projekt.posluzitelj.PokretacPosluzitelja $filename
