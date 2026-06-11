#!/bin/bash

echo "Waiting for PostgreSQL to start..." && \
until pg_isready -h "${POSTGRES_HOST}" -p "${POSTGRES_PORT}" -d "${POSTGRES_NAME}" -U "${POSTGRES_USER}"
do
    echo "PostgreSQL at ${POSTGRES_HOST}:${POSTGRES_PORT} is unavailable - sleeping"
    sleep 1
done

mvn clean generate-sources package -DskipTests

cp target/*.jar app.jar
echo "Starting the application..."
java -jar app.jar
