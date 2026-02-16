#!/bin/bash
echo "=== Docker Status ==="
docker --version
docker compose version

echo -e "\n=== Running Containers ==="
docker ps

echo -e "\n=== Temporal Logs ==="
docker compose -f docker-compose-simple.yml logs temporal | tail -20

echo -e "\n=== Port Check ==="
lsof -i :7233
lsof -i :8088

echo -e "\n=== Network Test ==="
nc -zv localhost 7233