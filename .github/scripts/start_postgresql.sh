#!/usr/bin/env bash
set -euo pipefail
set -x

if [[ "$(uname -s)" == "Linux" && -r /etc/os-release ]]; then
    sudo apt-get update
    sudo apt-get install -y podman podman-compose
fi

podman-compose up -d
