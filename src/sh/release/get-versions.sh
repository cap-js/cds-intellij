#!/bin/bash
set -eo pipefail

CURRENT_VERSION=$(grep '^pluginVersion' gradle.properties | cut -d'=' -f2 | tr -d ' ')
echo "current_plugin=$CURRENT_VERSION" >> "$GITHUB_OUTPUT"

LAST_TAG=$(git tag -l | grep -v -E '(legacy|rc)' | sort -V -r | head -n1)
echo "last_tag=$LAST_TAG" >> "$GITHUB_OUTPUT"

OLD_LSP=$(git show "$LAST_TAG:lsp/package.json" | jq -r '.dependencies["@sap/cds-lsp"]')
echo "old_lsp=$OLD_LSP" >> "$GITHUB_OUTPUT"

CURRENT_LSP=$(jq -r '.dependencies["@sap/cds-lsp"]' lsp/package.json)
echo "current_lsp=$CURRENT_LSP" >> "$GITHUB_OUTPUT"

INPUT_LSP="${1:-auto}"
if [ "$INPUT_LSP" = "auto" ]; then
  if [ "$OLD_LSP" = "$CURRENT_LSP" ]; then
    LSP_IMPACT=0
  else
    OLD_PARTS=(${OLD_LSP//./ })
    NEW_PARTS=(${CURRENT_LSP//./ })
    if [ "${OLD_PARTS[0]}" != "${NEW_PARTS[0]}" ]; then
      LSP_IMPACT=3
    elif [ "${OLD_PARTS[1]}" != "${NEW_PARTS[1]}" ]; then
      LSP_IMPACT=2
    else
      LSP_IMPACT=1
    fi
  fi
else
  LSP_IMPACT="$INPUT_LSP"
fi
echo "lsp_impact=$LSP_IMPACT" >> "$GITHUB_OUTPUT"
