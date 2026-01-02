#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

echo "========================================="
echo "Lab 06 - DDD - Database Reset"
echo "========================================="
echo ""

# Stop and remove containers
echo "1. Stopping containers..."
podman stop banking-ddd-app banking-ddd-db 2>/dev/null || true
podman rm banking-ddd-app banking-ddd-db 2>/dev/null || true
echo "✓ Containers removed"
echo ""

# Remove network
echo "2. Removing network..."
podman network rm banking-ddd-network 2>/dev/null || true
echo "✓ Network removed"
echo ""

# Remove volumes (THIS IS THE KEY - removes old database data)
echo "3. Removing volumes..."
podman volume rm banking-ddd-db-data 2>/dev/null || true
echo "✓ Volumes removed"
echo ""

echo "========================================="
echo "Database reset complete!"
echo "Run ./podman-test.sh to redeploy with fresh database"
echo "========================================="
