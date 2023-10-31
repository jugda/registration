#!/bin/bash
set -e
mvn clean package -Pnative -DskipTests
TENANT=jugda sls deploy
TENANT=cyberland sls deploy
