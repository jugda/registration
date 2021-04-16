#!/bin/sh
awslocal ses verify-email-identity --email-address info@jug-da.de
awslocal dynamodb create-table --cli-input-json file:///tmp/dynamodb-schema.json
awslocal s3api create-bucket --bucket test
awslocal s3 cp /tmp/events.json s3://test
awslocal s3 cp /tmp/eventData.json s3://test
