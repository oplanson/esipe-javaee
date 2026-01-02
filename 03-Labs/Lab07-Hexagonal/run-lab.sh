#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
# Run Lab 07 - Hexagonal Architecture

set -e

echo "========================================="
echo "Lab 07 - Hexagonal Architecture"
echo "========================================="

# Change to solution directory
cd solution

# Start PostgreSQL with Docker Compose
echo "Starting PostgreSQL..."
docker-compose up -d postgres

# Wait for PostgreSQL to be ready
echo "Waiting for PostgreSQL to be ready..."
sleep 5

# Run Flyway migrations
echo "Running database migrations..."
mvn flyway:migrate

# Build and run the application
echo "Building application..."
mvn clean package

echo "Starting Liberty server..."
mvn liberty:run

echo "========================================="
echo "Application started successfully!"
echo "Web UI: http://localhost:9080"
echo "REST API: http://localhost:9080/api"
echo "Health: http://localhost:9080/health"
echo "Metrics: http://localhost:9080/metrics"
echo "========================================="

# Made with Bob
