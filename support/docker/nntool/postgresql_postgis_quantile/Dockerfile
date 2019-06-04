FROM mdillon/postgis:9.6

MAINTAINER alladin-IT GmbH

RUN apt-get update
RUN apt-get install -y devscripts pgxnclient postgresql-server-dev-9.6
RUN pgxn install quantile

COPY schema.sql /tmp/nntool-schema.sql

# remove script from mdillon/postgis:9.6 (we don't need it)
RUN rm -f /docker-entrypoint-initdb.d/postgis.sh

COPY ./initdb-nntool.sh /docker-entrypoint-initdb.d/nntool.sh
