#!/usr/bin/env sh
set -eu

DOCKERHUB_USER="${1:-${DOCKERHUB_USER:-aaluc}}"
IMAGE_NAME="${2:-${IMAGE_NAME:-futbolmanager}}"
TAG="${3:-${TAG:-latest}}"

SCRIPT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
PROJECT_DIR="$(CDPATH= cd -- "${SCRIPT_DIR}/.." && pwd)"
FULL_IMAGE="${DOCKERHUB_USER}/${IMAGE_NAME}:${TAG}"

echo "Construyendo imagen ${FULL_IMAGE}..."
docker build -f "${SCRIPT_DIR}/Dockerfile" -t "${FULL_IMAGE}" "${PROJECT_DIR}"

echo "Imagen creada: ${FULL_IMAGE}"

