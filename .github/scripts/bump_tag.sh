#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'EOF'
Usage: .github/scripts/bump_tag.sh [--dry-run] [--push] [--verbose]

Creates a new semver tag based on existing tags and commit messages.

Version bump logic:
  - MAJOR: If Gradle major version is 1 ahead of latest tag, bump major and reset minor/patch to 0.
           Fails if Gradle major is 2+ ahead or behind the latest tag.
  - MINOR: If commit message contains "new-feature", bump minor and reset patch to 0.
  - PATCH: Otherwise, bump patch version.

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

IFS='.' read -r gradle_major _ _ <<<"$base_version"

# Check if last commit message contains "new-feature"
COMMIT_MSG=$(git log -1 --format=%s)
BUMP_MINOR=false
if [[ "$COMMIT_MSG" == *"new-feature"* ]]; then
  BUMP_MINOR=true
fi

# Find the latest tag
latest_tag=$(git tag --list "v[0-9]*.[0-9]*.[0-9]*" | LC_ALL=C sort -V | tail -n 1)

if [[ -n "$latest_tag" ]]; then
  latest_version=${latest_tag#v}
  IFS='.' read -r latest_major latest_minor latest_patch <<<"$latest_version"

  major_diff=$((gradle_major - latest_major))

  if [[ $major_diff -ge 2 ]]; then
    echo "Version from build.gradle.kts ($gradle_major) is 2 or more ahead of latest tag major version ($latest_major)"
    exit 1
  elif [[ $major_diff -eq 1 ]]; then
    # Bump to new major version, reset minor and patch
    major=$gradle_major
    minor=0
    patch=0
  elif [[ $major_diff -lt 0 ]]; then
    echo "Error: Gradle major version ($gradle_major) is behind latest tag major version ($latest_major)"
    exit 1
  elif $BUMP_MINOR; then
    # Bump minor version, reset patch to 0
    major=$latest_major
    minor=$((latest_minor + 1))
    patch=0
  else
    # Bump patch version
    major=$latest_major
    minor=$latest_minor
    patch=$((latest_patch + 1))
  fi
else
  # No existing tags, start fresh
  major=$gradle_major
  if $BUMP_MINOR; then
    minor=1
    patch=0
  else
    minor=0
    patch=0
  fi
fi

new_version="${major}.${minor}.${patch}"
new_tag="v${new_version}"

if $VERBOSE; then
  echo "Gradle major version: ${gradle_major}"
  echo "Commit message: ${COMMIT_MSG}"
  if [[ -n "$latest_tag" ]]; then
    echo "Latest tag: ${latest_tag} (major: ${latest_major})"
  else
    echo "Latest tag: (none)"
  fi
  if [[ -n "$latest_tag" ]] && [[ $major_diff -eq 1 ]]; then
    echo "Bump type: MAJOR (gradle version ahead)"
  elif $BUMP_MINOR; then
    echo "Bump type: MINOR (new-feature)"
  else
    echo "Bump type: PATCH"
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
