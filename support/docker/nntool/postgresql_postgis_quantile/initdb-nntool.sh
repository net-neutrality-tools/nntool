#!/bin/sh

set -e

# Perform all actions as $POSTGRES_USER
export PGUSER="$POSTGRES_USER"

# Create the 'template_postgis' template db
"${psql[@]}" <<- 'EOSQL'
CREATE DATABASE nntool;
CREATE USER nntool WITH PASSWORD 'nntool';
GRANT ALL PRIVILEGES ON DATABASE nntool to nntool;
EOSQL

echo "Loading nntool related extensions into $DB"
"${psql[@]}" --dbname="nntool" <<-'EOSQL'
  CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public;
  CREATE EXTENSION IF NOT EXISTS quantile WITH SCHEMA public;
EOSQL

cat /tmp/nntool-schema.sql | "${psql[@]}" --dbname="nntool"
