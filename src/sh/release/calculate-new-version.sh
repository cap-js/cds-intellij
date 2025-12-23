#!/bin/bash
set -eo pipefail

CURRENT_VERSION="${1}"
LSP_IMPACT="${2:-0}"
PLUGIN_IMPACT="${3:-auto}"
LAST_TAG="${4}"

if [[ -z "$CURRENT_VERSION" || -z "$LAST_TAG" ]]; then
  echo "Usage: $0 <current-version> <lsp-impact> <plugin-impact> <last-tag>" >&2
  exit 1
fi

# Accept numeric or auto
if [[ "$LSP_IMPACT" =~ ^[0-3]$ ]]; then
  LSP_BUMP=$LSP_IMPACT
else
  echo "Error: Invalid lsp_impact: $LSP_IMPACT (expected 0-3)" >&2
  exit 1
fi

if [[ "$PLUGIN_IMPACT" == "auto" ]]; then
  if git log $LAST_TAG..HEAD --format="%B" | grep -q "BREAKING"; then
    PLUGIN_BUMP=3
  elif git log $LAST_TAG..HEAD --oneline | grep -qE "^[a-f0-9]+ [a-z]+(\(.+\))?!:"; then
    PLUGIN_BUMP=3
  elif git log $LAST_TAG..HEAD --oneline | grep -qE "^[a-f0-9]+ feat"; then
    PLUGIN_BUMP=2
  else
    PLUGIN_BUMP=1
  fi
elif [[ "$PLUGIN_IMPACT" =~ ^[0-3]$ ]]; then
  PLUGIN_BUMP=$PLUGIN_IMPACT
else
  echo "Error: Invalid plugin_impact: $PLUGIN_IMPACT (expected 0-3 or auto)" >&2
  exit 1
fi

FINAL_BUMP=$((LSP_BUMP > PLUGIN_BUMP ? LSP_BUMP : PLUGIN_BUMP))

if [[ $FINAL_BUMP -eq 0 ]]; then
  echo "Error: No version bump needed" >&2
  exit 1
fi

# Calculate new version
IFS='.' read -r major minor patch <<< "$CURRENT_VERSION"

case $FINAL_BUMP in
  3)
    major=$((major + 1))
    minor=0
    patch=0
    BUMP_NAME="major"
    ;;
  2)
    minor=$((minor + 1))
    patch=0
    BUMP_NAME="minor"
    ;;
  1)
    patch=$((patch + 1))
    BUMP_NAME="patch"
    ;;
esac

NEW_VERSION="$major.$minor.$patch"

# Output all values
echo "version=$NEW_VERSION" >> "$GITHUB_OUTPUT"
echo "bump_level=$BUMP_NAME" >> "$GITHUB_OUTPUT"
echo "lsp_bump_level=$LSP_BUMP" >> "$GITHUB_OUTPUT"
echo "plugin_bump_level=$PLUGIN_BUMP" >> "$GITHUB_OUTPUT"
