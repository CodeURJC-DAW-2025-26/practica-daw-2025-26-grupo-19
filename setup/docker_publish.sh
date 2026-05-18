#!/usr/bin/env sh
set -eu

DOCKERHUB_USER="${1:-${DOCKERHUB_USER:-aaluc}}"
IMAGE_NAME="${2:-${IMAGE_NAME:-futbolmanager}}"
TAG="${3:-${TAG:-latest}}"

SCRIPT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"

sh "${SCRIPT_DIR}/docker_build.sh" "${DOCKERHUB_USER}" "${IMAGE_NAME}" "${TAG}"
sh "${SCRIPT_DIR}/docker_push.sh" "${DOCKERHUB_USER}" "${IMAGE_NAME}" "${TAG}"
