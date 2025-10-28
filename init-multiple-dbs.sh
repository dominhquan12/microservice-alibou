#!/bin/bash
set -e

echo "Multiple databases creation requested: $POSTGRES_MULTIPLE_DATABASES"

for db in $(echo $POSTGRES_MULTIPLE_DATABASES | tr ',' ' '); do
  echo "Creating database: $db"
  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "postgres" <<-EOSQL
      CREATE DATABASE "$db";
EOSQL
done

echo "All databases created successfully!"
