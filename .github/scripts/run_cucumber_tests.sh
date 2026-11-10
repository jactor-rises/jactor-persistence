#!/usr/bin/env bash
set -euo pipefail

script_directory="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
repo_root="$(cd -- "$script_directory/../.." && pwd)"

cd "$repo_root/cucumber-persistence"
mvn test
