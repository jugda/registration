#!/bin/sh
TENANT=${1:-jugda}
mvn clean package -Pnative && sls deploy --tenant ${TENANT}
