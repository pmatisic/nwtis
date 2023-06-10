#!/bin/bash

# Promjena verzije Jave u 17
export JAVA_HOME=/usr/lib/jvm/jdk-17.0.2
export PATH=/usr/lib/jvm/jdk-17.0.2/bin:$PATH

# Pokretanje baze podataka
./scripts/baza.sh

# Funkcija za pokretanje Payara Full servera
pokreni_payara_full_server() {
    echo "Pokrećem Payara Full server..."
    cd /opt/payara-full-6.2023.4/glassfish/bin
    ./asadmin start-domain
}

# Funkcija za zaustavljanje Payara Full servera
zaustavi_payara_full_server() {
    echo "Moguće je da već postoji instanca Payara Full servera. Zaustavljam server..."
    cd /opt/payara-full-6.2023.4/glassfish/bin
    if ./asadmin list-domains | grep -q "domain1 running"; then
        ./asadmin stop-domain
    fi
}

# Funkcija za zaustavljanje Payara Web servera
zaustavi_payara_web_server() {
    echo "Moguće je da već postoji instanca Payara Web servera. Zaustavljam server..."
    cd /opt/payara-web-6.2023.4/glassfish/bin
    if ./asadmin list-domains | grep -q "domain1 running"; then
        ./asadmin stop-domain
    fi
}

# Provjera je li port 4848 zauzet
port_zauzet=$(netstat -tuln | grep -w 4848)

# Ako port nije zauzet, pokreni Payara Full server
if [ -z "$port_zauzet" ]; then
    pokreni_payara_full_server
else
    zaustavi_payara_web_server
    zaustavi_payara_full_server
    pokreni_payara_full_server
fi
