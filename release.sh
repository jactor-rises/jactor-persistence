#!/bin/bash
set -e

# Hent SNAPSHOT version (og fjern SNAPSHOT)
RELEASE_VERSION="v$(cat pom.xml | grep SNAPSHOT | sed 's;version;;g' | sed 's;[< />];;g' | sed 's;-SNAPSHOT;;')"
echo "Will release $RELEASE_VERSION"

# Tag release
git tag -a "$RELEASE_VERSION" -m "Released $RELEASE_VERSION"
git push origin "$RELEASE_VERSION"

# Release with maven
mvn -B --settings maven-settings.xml deploy -Dmaven.wagon.http.pool=false

# Fetch major and patch version
MAJOR_VERSION=${RELEASE_VERSION%.*}
PATCH_VERSION=$(echo "$RELEASE_VERSION" | sed "s;$MAJOR_VERSION.;;")
echo "Major version: $MAJOR_VERSION, patch version: $PATCH_VERSION"

# Update to next SNAPSHOT version
NEXT_SNAPSHOT="$MAJOR_VERSION.$((PATCH_VERSION+1))-SNAPSHOT"
mvn -B versions:set -DnewVersion="$NEXT_SNAPSHOT"
git add pom.xml
git commit -m "New SNAPSHOT version: $NEXT_SNAPSHOT"
