curl -X POST \
  http://localhost:15984/settings/_bulk_docs \
  -H 'Content-Type: application/json' \
  -H 'cache-control: no-cache' \
  -d @${BASH_SOURCE%/*}/default_couchdb_data_docker.json


curl -X POST \
  http://localhost:15984/measurement/_index \
  -H 'Content-Type: application/json' \
  -H 'cache-control: no-cache' \
  -d @${BASH_SOURCE%/*}/default_couchdb_data_measurement_docker.json