#!/bin/bash
set -euo pipefail

# Reset change-notes in plugin.xml after release
# Usage: reset-changelog.sh

PLUGIN_XML="src/main/resources/META-INF/plugin.xml"

if [[ ! -f "$PLUGIN_XML" ]]; then
  echo "Error: $PLUGIN_XML not found" >&2
  exit 1
fi

# Find the change-notes section and replace with placeholder
awk '
  /<change-notes>/ { 
    in_notes = 1
    print "    <change-notes><![CDATA["
    print "<h3>Unreleased</h3>"
    print "<ul>"
    print "    <li>See <a href=\"https://github.com/cap-js/cds-intellij/commits/main\">commit history</a> for latest changes</li>"
    print "</ul>"
    next
  }
  /]]><\/change-notes>/ {
    if (in_notes) {
      print "]]></change-notes>"
      in_notes = 0
      next
    }
  }
  !in_notes { print }
' "$PLUGIN_XML" > "$PLUGIN_XML.tmp"

mv "$PLUGIN_XML.tmp" "$PLUGIN_XML"
