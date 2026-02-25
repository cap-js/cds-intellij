#!/bin/bash
set -eo pipefail

NEW_VERSION="${1}"

if [[ -z "$NEW_VERSION" ]]; then
  echo "Usage: $0 <new-version>" >&2
  exit 1
fi

sed -i "s/^pluginVersion\b.*/pluginVersion = $NEW_VERSION/" gradle.properties
