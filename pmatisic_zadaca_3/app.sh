#!/bin/bash

# Promjena verzije Jave u 17
export JAVA_HOME=/usr/lib/jvm/jdk-17.0.2
export PATH=/usr/lib/jvm/jdk-17.0.2/bin:$PATH

# Provjera da li je Payara server pokrenut
cd /opt/payara-full-6.2023.4/glassfish/bin
if ./asadmin list-domains | grep -q "domain1 running"; then
    echo "Server is running. Stopping the server..."
    ./asadmin stop-domain
    echo "Starting the server again..."
    ./asadmin start-domain
else
    echo "Server is not running."
    echo "Starting the server..."
    ./asadmin start-domain
fi

# Pokretanje baze
cd /opt/hsqldb-2.7.1/hsqldb/data
echo "Starting the database..."
sudo java -classpath ../lib/hsqldb.jar org.hsqldb.server.Server \
--database.0 file:nwtis_4 --dbname.0 nwtis_4 --port 9001
