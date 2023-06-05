#!/bin/bash

# Funkcija za pokretanje Payara Servera
pokreni_payara_server() {
    export JAVA_HOME=/usr/lib/jvm/jdk-17.0.2
    export PATH=/usr/lib/jvm/jdk-17.0.2/bin:$PATH
    sudo chmod -R g+w /opt/payara-web-6.2023.1/glassfish/domains/domain1/applications/
    sudo chmod -R g+w /opt/payara-web-6.2023.1/glassfish/domains/domain1/generated/
    cd /opt/payara-web-6.2023.1/glassfish/bin
    ./asadmin start-domain
}

# Provjera da li je port 4848 zauzet
port_zauzet=$(netstat -tuln | grep -w 4848)

# Ako port nije zauzet, pokreni Payara Server
if [ -z "$port_zauzet" ]; then
    pokreni_payara_server
else
    echo "Port 4848 je već zauzet. Moguće je da već postoji instanca Payara Servera ili Payara Micro."
    echo "Naredba start-domain nije uspjela."
    echo "Zaustavljam domenu i ponovno pokrećem proces..."
    cd /opt/payara-web-6.2023.1/glassfish/bin
    ./asadmin stop-domain
    pokreni_payara_server
fi
