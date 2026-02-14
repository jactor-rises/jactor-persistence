#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'EOF'
Usage: .github/scripts/bump_tag.sh [--dry-run] [--push] [--verbose]

Creates a new semver tag based on the Gradle project version and existing tags.
Uses Gradle major/minor and bumps the patch for matching tags.

Options:
  --dry-run   Print the tag that would be created, but do not create it.
  --push      Push the created tag to origin.
  --verbose   Print details about the version and tag selection.
EOF
}

DRY_RUN=false
PUSH=false
VERBOSE=false

for arg in "$@"; do
  case "$arg" in
    --dry-run) DRY_RUN=true ;;
    --push) PUSH=true ;;
    --verbose) VERBOSE=true ;;
    -h|--help) usage; exit 0 ;;
    *) echo "Unknown argument: $arg"; usage; exit 1 ;;
  esac
done

if [[ ! -f ./gradlew ]]; then
  echo "gradlew not found in the current directory"
  exit 1
fi

base_version=$(./gradlew -q printVersion | sed 's/-SNAPSHOT$//')
if [[ ! "$base_version" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
  echo "Gradle version is not semver (X.Y.Z): $base_version"
  exit 1
fi

IFS='.' read -r base_major base_minor base_patch <<<"$base_version"
matching_tag=$(git tag --list "v${base_major}.${base_minor}.[0-9]*" | LC_ALL=C sort -V | tail -n 1)

if [[ -n "$matching_tag" ]]; then
  matching_version=${matching_tag#v}
  IFS='.' read -r _ _ matching_patch <<<"$matching_version"
  patch=$((matching_patch + 1))
else
  patch=0
fi

new_version="${base_major}.${base_minor}.${patch}"
new_tag="v${new_version}"

if $VERBOSE; then
  echo "Gradle version: ${base_version}"
  if [[ -n "$matching_tag" ]]; then
    echo "Matching tag: ${matching_tag}"
  else
    echo "Matching tag: (none)"
  fi
  echo "Next tag: ${new_tag}"
fi

if $DRY_RUN; then
  echo "$new_tag"
  exit 0
fi

git tag -a "$new_tag" -m "Release $new_tag"
if $PUSH; then
  git push origin "$new_tag"
fi

echo "$new_tag"
