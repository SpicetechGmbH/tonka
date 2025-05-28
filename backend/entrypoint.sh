#!/bin/bash

echo "Waiting for PostgreSQL to start..." && \
until pg_isready -h dts-os-db -U user -d postgres; do sleep 1; done

mvn clean generate-sources package -DskipTests

cp target/*.jar app.jar
echo "Starting the application..."
java -jar app.jar
