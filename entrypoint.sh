#!/bin/sh
set -e

# ───────── SSM CONFIG ─────────
export SERVER_PORT=$(aws ssm get-parameter --name "/insurtech/dev/SERVER_PORT" --query "Parameter.Value" --output text)
export SPRING_PROFILES_ACTIVE=$(aws ssm get-parameter --name "/insurtech/dev/SPRING_PROFILES_ACTIVE" --query "Parameter.Value" --output text)
export S3_BUCKET=$(aws ssm get-parameter --name "/insurtech/dev/S3_BUCKET" --query "Parameter.Value" --output text)
export AWS_REGION="eu-central-1"
export AWS_DEFAULT_REGION="eu-central-1"
export AI_SERVICE_URL=$(aws ssm get-parameter --name "/insurtech/dev/AI_SERVICE_URL" --query "Parameter.Value" --output text)
export AI_ANALYZE_URI=$(aws ssm get-parameter --name "/insurtech/dev/AI_ANALYZE_URI" --query "Parameter.Value" --output text)

# ───────── DB SECRET ─────────
DB_SECRET=$(aws secretsmanager get-secret-value \
  --secret-id "/insurtech/dev/db" \
  --query "SecretString" --output text)

export DB_HOST=$(echo "$DB_SECRET" | jq -r '.DB_HOST')
export DB_PORT=$(echo "$DB_SECRET" | jq -r '.DB_PORT')
export DB_NAME=$(echo "$DB_SECRET" | jq -r '.DB_NAME')
export DB_USERNAME=$(echo "$DB_SECRET" | jq -r '.DB_USERNAME')
export DB_PASSWORD=$(echo "$DB_SECRET" | jq -r '.DB_PASSWORD')

# ───────── JWT SECRET ─────────
JWT_SECRET=$(aws secretsmanager get-secret-value \
  --secret-id "/insurtech/dev/jwt" \
  --query "SecretString" --output text)

export JWT_PRIVATE_KEY=$(echo "$JWT_SECRET" | jq -r '.JWT_PRIVATE_KEY')
export JWT_PUBLIC_KEY=$(echo "$JWT_SECRET" | jq -r '.JWT_PUBLIC_KEY')
export JWT_ISSUER=$(echo "$JWT_SECRET" | jq -r '.JWT_ISSUER')
export JWT_AUDIENCE=$(echo "$JWT_SECRET" | jq -r '.JWT_AUDIENCE')
export ACCESS_TOKEN_TTL=$(echo "$JWT_SECRET" | jq -r '.ACCESS_TOKEN_TTL')
export REFRESH_TOKEN_TTL_DAYS=$(echo "$JWT_SECRET" | jq -r '.REFRESH_TOKEN_TTL_DAYS')

# ───────── RUN APP ─────────
exec java \
  -XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=75.0 \
  -Djava.security.egd=file:/dev/./urandom \
  -jar app.jar