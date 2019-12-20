FROM mdillon/postgis:11

MAINTAINER alladin-IT GmbH

RUN apt-get update ; apt-get upgrade -y

COPY schema.sql /tmp/nntool-schema.sql

# remove script from mdillon/postgis:11 (we don't need it)
RUN rm -f /docker-entrypoint-initdb.d/postgis.sh

COPY ./initdb-nntool.sh /docker-entrypoint-initdb.d/nntool.sh
