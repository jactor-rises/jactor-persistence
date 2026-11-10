#!/usr/bin/env bash
set -euo pipefail

script_directory="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
repo_root="$(cd -- "$script_directory/../.." && pwd)"
workspace="${GITHUB_WORKSPACE:-$repo_root}"

# Keep the PID filename in sync with start_spring_boot.sh.
pid_file="$workspace/jactor-persistence-spring-boot.pid"

if [[ ! -f "$pid_file" ]]; then
  echo "Spring Boot PID file not found; nothing to stop."
  exit 0
fi

spring_boot_pid="$(<"$pid_file")"

process_group_id="$(ps -o pgid= -p "$spring_boot_pid" | tr -d '[:space:]')"
if [[ "$process_group_id" == "$spring_boot_pid" ]]; then
  kill -TERM -- "-$spring_boot_pid"
else
  kill -TERM "$spring_boot_pid"
fi
rm -f -- "$pid_file"
