#!/bin/bash

# SQSキューの作成
aws --endpoint-url=http://localhost:4566 --region us-east-1 sqs create-queue --queue-name create-keyword-tree-queue

# SQSキューをリスト表示（確認用）
# aws --endpoint-url=http://localhost:4566 --region us-east-1 sqs list-queues