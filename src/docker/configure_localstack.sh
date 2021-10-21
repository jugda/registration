#!/bin/sh
awslocal ses verify-email-identity --email-address info@jug-da.de --region eu-central-1
awslocal dynamodb create-table --cli-input-json file:///tmp/dynamodb-schema.json --region eu-central-1
awslocal s3api create-bucket --bucket test --region eu-central-1
awslocal s3 cp /tmp/events.json s3://test --region eu-central-1
awslocal s3 cp /tmp/eventData.json s3://test --region eu-central-1
