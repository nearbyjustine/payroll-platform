#!/bin/bash
# Runs inside LocalStack once it's ready (mounted into /etc/localstack/init/ready.d).
set -euo pipefail
awslocal s3 mb s3://payslips
echo "payroll: bucket 'payslips' created"
