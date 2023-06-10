#!/bin/bash

# Funkcija za pokretanje Payara servera
pokreni_payara_server() {
    export JAVA_HOME=/usr/lib/jvm/jdk-17.0.2
    export PATH=/usr/lib/jvm/jdk-17.0.2/bin:$PATH
    cd /opt/payara-web-6.2023.4/glassfish/bin
    ./asadmin start-domain
}

# Funkcija za zaustavljanje Payara servera
zaustavi_payara_server() {
    echo "Port 4848 je već zauzet. Moguće je da već postoji instanca Payara Full ili Payara Micro."
    echo "Naredba start-domain nije uspjela."
    echo "Zaustavljam domenu i ponovno pokrećem proces..."
    # Provjera je li Payara Full pokrenut
    cd /opt/payara-full-6.2023.4/glassfish/bin
    if ./asadmin list-domains | grep -q "domain1 running"; then
        ./asadmin stop-domain
    fi
    # Provjera je li Payara Micro pokrenut
    cd /opt/payara-web-6.2023.4/glassfish/bin
    if ./asadmin list-domains | grep -q "domain1 running"; then
        ./asadmin stop-domain
    fi
}

# Provjera je li port 4848 zauzet
port_zauzet=$(netstat -tuln | grep -w 4848)

# Ako port nije zauzet, pokreni Payara server
if [ -z "$port_zauzet" ]; then
    pokreni_payara_server
else
    zaustavi_payara_server
    pokreni_payara_server
fi
