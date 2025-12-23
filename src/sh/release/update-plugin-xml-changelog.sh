#!/bin/bash
set -eo pipefail

CHANGELOG="${1}"
PLUGIN_XML="${2:-src/main/resources/META-INF/plugin.xml}"

if [[ -z "$CHANGELOG" ]]; then
  echo "Usage: $0 <changelog-content> [plugin-xml-path]" >&2
  exit 1
fi

awk -v changelog="$CHANGELOG" '
  BEGIN { in_notes=0 }
  /<change-notes><!\[CDATA\[/ {
    print
    print changelog
    in_notes=1
    next
  }
  /\]\]><\/change-notes>/ {
    if (in_notes) {
      print
      in_notes=0
      next
    }
  }
  !in_notes { print }
' "$PLUGIN_XML" > "$PLUGIN_XML.tmp"

mv "$PLUGIN_XML.tmp" "$PLUGIN_XML"
