#!/usr/bin/env sh
set -eu

DOCKERHUB_USER="${1:-${DOCKERHUB_USER:-aaluc}}"

echo "Iniciando sesion en Docker Hub como ${DOCKERHUB_USER}..."
docker login -u "${DOCKERHUB_USER}"

