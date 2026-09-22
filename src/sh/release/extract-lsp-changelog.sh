#!/bin/bash
set -eo pipefail

# Extract changelog entries from @sap/cds-lsp CHANGELOG.md between two versions
# Usage: extract-lsp-changelog.sh <from-version> <to-version>
#   from-version: Starting version (exclusive)
#   to-version: Ending version (inclusive)
#
# The script tries to find CHANGELOG.md in this order:
# 1. Local sibling directory: ../cds-lsp/CHANGELOG.md (relative to repo root)
# 2. Extract from npm package @sap/cds-lsp@<to-version>

FROM_VERSION="${1:-}"
TO_VERSION="${2:-}"

if [[ -z "$FROM_VERSION" || -z "$TO_VERSION" ]]; then
  echo "Usage: $0 <from-version> <to-version>" >&2
  echo "  from-version: Starting version (exclusive)" >&2
  echo "  to-version: Ending version (inclusive)" >&2
  exit 1
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../../.." && pwd)"
LOCAL_CHANGELOG="$REPO_ROOT/../cds-lsp/CHANGELOG.md"

CHANGELOG_CONTENT=""

if [[ -f "$LOCAL_CHANGELOG" ]]; then
  CHANGELOG_CONTENT=$(cat "$LOCAL_CHANGELOG")
else
  TEMP_DIR=$(mktemp -d)
  trap 'rm -rf "$TEMP_DIR"' EXIT
  
  if npm pack "@sap/cds-lsp@$TO_VERSION" --pack-destination "$TEMP_DIR" --silent >/dev/null 2>&1; then
    tar -xzf "$TEMP_DIR"/*.tgz -C "$TEMP_DIR" 2>/dev/null
    if [[ -f "$TEMP_DIR/package/CHANGELOG.md" ]]; then
      CHANGELOG_CONTENT=$(cat "$TEMP_DIR/package/CHANGELOG.md")
    fi
  fi
  
  if [[ -z "$CHANGELOG_CONTENT" ]]; then
    echo "Warning: Could not extract CHANGELOG.md from @sap/cds-lsp@$TO_VERSION" >&2
    exit 0
  fi
fi

normalize_version() {
  echo "$1" | sed 's/^v//'
}

FROM_VERSION=$(normalize_version "$FROM_VERSION")
TO_VERSION=$(normalize_version "$TO_VERSION")

compare_versions() {
  local v1="$1" v2="$2"
  
  IFS='.' read -ra V1_PARTS <<< "$v1"
  IFS='.' read -ra V2_PARTS <<< "$v2"
  
  for i in 0 1 2; do
    local p1="${V1_PARTS[$i]:-0}"
    local p2="${V2_PARTS[$i]:-0}"
    if (( p1 > p2 )); then
      echo "1"
      return
    elif (( p1 < p2 )); then
      echo "-1"
      return
    fi
  done
  echo "0"
}

# Escape HTML-special characters, then turn backtick spans into <code> tags.
to_html() {
  local text="$1"
  text="${text//&/\&amp;}"
  text="${text//</\&lt;}"
  text="${text//>/\&gt;}"
  text="$(printf '%s' "$text" | sed -E 's/`([^`]*)`/<code>\1<\/code>/g')"
  printf '%s' "$text"
}

capitalize() {
  local text="$1"
  printf '%s%s' "$(printf '%s' "${text:0:1}" | tr '[:lower:]' '[:upper:]')" "${text:1}"
}

in_range=false
current_section=""
# Each item is stored as "depth<TAB>html-text". depth 0 = top-level, 1 = nested.
declare -a added_items removed_items fixed_items

while IFS= read -r line; do
  if [[ $line =~ ^##\ ([0-9]+\.[0-9]+\.[0-9]+) ]]; then
    version="${BASH_REMATCH[1]}"

    cmp_to=$(compare_versions "$version" "$TO_VERSION")
    cmp_from=$(compare_versions "$version" "$FROM_VERSION")

    if [[ "$cmp_from" == "0" || "$cmp_from" == "-1" ]]; then
      break
    fi

    if [[ "$cmp_to" == "0" || "$cmp_to" == "-1" ]]; then
      in_range=true
      continue
    fi
  fi

  if [[ "$in_range" != "true" ]]; then
    continue
  fi

  if [[ $line =~ ^###\ (.+) ]]; then
    current_section="${BASH_REMATCH[1]}"
    continue
  fi

  if [[ $line =~ ^([[:space:]]*)[-+][[:space:]](.+) ]]; then
    indent="${BASH_REMATCH[1]}"
    text="${BASH_REMATCH[2]}"
    depth=0
    [[ -n "$indent" ]] && depth=1

    case "$current_section" in
      Added|Changed)
        if [[ $depth -eq 0 ]]; then
          text="$(capitalize "$text")"
        fi
        added_items+=("$depth"$'\t'"$(to_html "$text")")
        ;;
      Removed)
        if [[ $depth -eq 0 ]]; then
          text="Removed: $(capitalize "$text")"
        fi
        removed_items+=("$depth"$'\t'"$(to_html "$text")")
        ;;
      Fixed)
        if [[ $depth -eq 0 ]]; then
          text="$(printf '%s' "$text" | sed -nE 's/^ *fixed[: ]*//I;p')"
          text="Fixed: $(capitalize "$text")"
        fi
        fixed_items+=("$depth"$'\t'"$(to_html "$text")")
        ;;
    esac
  fi
done <<< "$CHANGELOG_CONTENT"

# A top-level <li> is held back until we know whether nested children follow.
render_items() {
  local -n items=$1
  local pending=""
  local child_open=false
  local depth text
  flush_pending() {
    [[ -n "$pending" ]] && echo "            <li>$pending</li>"
    pending=""
  }
  for entry in "${items[@]}"; do
    depth="${entry%%$'\t'*}"
    text="${entry#*$'\t'}"
    if [[ "$depth" -eq 0 ]]; then
      if [[ "$child_open" == "true" ]]; then
        echo "                </ul>"
        echo "            </li>"
        child_open=false
      else
        flush_pending
      fi
      pending="$text"
    else
      if [[ "$child_open" != "true" ]]; then
        # A nested bullet without a preceding top-level one becomes top-level.
        if [[ -z "$pending" ]]; then
          echo "            <li>$text</li>"
          continue
        fi
        echo "            <li>$pending"
        pending=""
        echo "                <ul>"
        child_open=true
      fi
      echo "                    <li>$text</li>"
    fi
  done
  if [[ "$child_open" == "true" ]]; then
    echo "                </ul>"
    echo "            </li>"
  else
    flush_pending
  fi
}

echo "        <ul>"
render_items added_items
render_items removed_items
render_items fixed_items
echo "        </ul>"
