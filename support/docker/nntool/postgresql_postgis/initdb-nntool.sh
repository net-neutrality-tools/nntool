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

export PGUSER="nntool"

echo "Loading nntool related extensions into $DB"
"${psql[@]}" --dbname="nntool" <<-'EOSQL'
  GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO nntool;
  GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO nntool;
  ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT USAGE, SELECT ON SEQUENCES TO nntool;
  ALTER SCHEMA public OWNER TO nntool;

  CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public;
EOSQL

cat /tmp/nntool-schema.sql | "${psql[@]}" --dbname="nntool"
