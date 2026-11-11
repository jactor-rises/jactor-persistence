#!/usr/bin/env bash
set -euo pipefail
set -x

script_directory="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
repo_root="$(cd -- "$script_directory/../.." && pwd)"
workspace="${GITHUB_WORKSPACE:-$repo_root}"
health_url="http://localhost:1099/jactor-persistence/actuator/health"

# Keep the PID filename in sync with stop_spring_boot.sh.
pid_file="$workspace/jactor-persistence-spring-boot.pid"

cd "$repo_root"
if command -v setsid >/dev/null 2>&1; then
  echo "Running with setsid"
  setsid ./gradlew --no-daemon bootRun > bootRun.log 2>&1 < /dev/null &
else
  echo "Do not run with setsid"
  ./gradlew --no-daemon bootRun > bootRun.log 2>&1 < /dev/null &
fi

spring_boot_pid=$!
printf '%s\n' "$spring_boot_pid" > "$pid_file"
set +x
echo "> spring boot prosess to stop: $(cat "$pid_file")"
echo "> ---------------------------"

deadline=$((SECONDS + 90))
while (( SECONDS < deadline )); do
  if curl -fsS --max-time 5 "$health_url" | jq -e 'any(.. | objects; .status? == "UP")' >/dev/null; then

    # If at least two users are present, the database migration is successful
    podman-compose exec -T database psql -U postgres -d postgres -c 'SELECT * FROM t_user;'

    exit 0
  fi
  sleep 2
done

echo "Spring Boot did not become healthy within 30 seconds."
exit 1
