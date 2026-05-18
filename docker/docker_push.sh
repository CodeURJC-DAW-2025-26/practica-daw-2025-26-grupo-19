#!/usr/bin/env sh
set -eu

DOCKERHUB_USER="${1:-${DOCKERHUB_USER:-aaluc}}"
IMAGE_NAME="${2:-${IMAGE_NAME:-futbolmanager}}"
TAG="${3:-${TAG:-latest}}"

FULL_IMAGE="${DOCKERHUB_USER}/${IMAGE_NAME}:${TAG}"

echo "Subiendo imagen ${FULL_IMAGE} a Docker Hub..."
docker push "${FULL_IMAGE}"

echo "Imagen subida: ${FULL_IMAGE}"

