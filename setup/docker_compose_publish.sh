#!/bin/bash

if [ "$#" -lt 1 ] || [ "$#" -gt 2 ]; then
	echo "Use: $0 <DockerHub_username> [tag]"
	echo "Example: $0 aaluc 1.0.0"
	exit 1
fi

USERNAME="$1"
TAG="${2:-latest}"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

if [ -z "$USERNAME" ] || [ -z "$TAG" ]; then
	echo "Use: $0 <DockerHub_username> [tag]"
	echo "Example: $0 aaluc 1.0.0"
	exit 1
fi

docker compose -f "$SCRIPT_DIR/docker-compose.yaml" publish "$USERNAME/futbolmanager-compose:$TAG"
