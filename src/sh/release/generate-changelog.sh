#!/bin/bash
set -eo pipefail  # Removed -u to simplify array handling

# Generate changelog HTML from conventional commits for plugin.xml
# Usage: generate-changelog.sh [from-ref] [to-ref] [to-version] [old-lsp] [new-lsp]
#   from-ref: Start reference (default: last tag)
#   to-ref: End reference (default: HEAD)
#   to-version: Target version for compare link (default: to-ref)
#   old-lsp: Previous @sap/cds-lsp version (optional)
#   new-lsp: Current @sap/cds-lsp version (optional)

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

FROM_REF="${1:-}"
TO_REF="${2:-HEAD}"
TO_VERSION="${3:-$TO_REF}"
OLD_LSP="${4:-}"
NEW_LSP="${5:-}"

# Find last tag if not specified
if [[ -z "$FROM_REF" ]]; then
  FROM_REF=$(git describe --tags --abbrev=0 2>/dev/null || echo "")
  if [[ -z "$FROM_REF" ]]; then
    echo "Error: No tags found. Please specify a from-ref." >&2
    exit 1
  fi
fi

# Categorize commits into user-facing changelog sections
# Only three sections: Added (feat), Changed (improvements), Fixed (fix)
declare -A commits_added
declare -A commits_changed
declare -A commits_fixed

while IFS= read -r line; do
  # Parse conventional commit format: type(scope): message or type: message
  regex='^([a-z]+)(\([^)]+\))?:[[:space:]](.+)$'
  if [[ $line =~ $regex ]]; then
    type="${BASH_REMATCH[1]}"
    scope="${BASH_REMATCH[2]}"  # includes parentheses if present
    message="${BASH_REMATCH[3]}"
    
    case "$type" in
      feat)
        # New features → Added
        commits_added["$message"]=1
        ;;
      fix)
        # Bugfixes → Fixed
        commits_fixed["$message"]=1
        ;;
      perf)
        # Performance improvements → Changed
        commits_changed["$message"]=1
        ;;
      chore)
        # Only include user-facing chores (lsp4ij upgrades)
        # Note: cds-lsp upgrades are handled separately with detailed changelog
        if [[ $message =~ lsp4ij ]]; then
          commits_changed["$message"]=1
        fi
        ;;
      # Ignore: refactor, docs, test, build, ci (all internal/non-user-facing)
    esac
  fi
done < <(git log --oneline --no-merges --format="%s" "$FROM_REF..$TO_REF")

# Generate HTML output - only show non-empty sections
output=""
has_content=false

# Helper to output messages from an array
output_messages() {
  local -n msgs=$1
  
  for msg in "${!msgs[@]}"; do
    # Remove PR number if present
    clean_msg="$msg"
    if [[ $msg =~ \(#([0-9]+)\)$ ]]; then
      clean_msg="${msg% (#*)}"
    fi
    
    # Capitalize first letter
    clean_msg="$(echo "${clean_msg:0:1}" | tr '[:lower:]' '[:upper:]')${clean_msg:1}"
    
    output+="    <li>$clean_msg</li>"$'\n'
  done
}

# Output sections in order: Added, Changed, Fixed
if [[ ${#commits_added[@]} -gt 0 ]]; then
  has_content=true
  output+="<h4>Added</h4>"$'\n'
  output+="<ul>"$'\n'
  output_messages commits_added
  output+="</ul>"$'\n'
fi

# Changed section - only show if there's content
lsp_changelog=""
if [[ -n "$OLD_LSP" && -n "$NEW_LSP" && "$OLD_LSP" != "$NEW_LSP" ]]; then
  lsp_changelog=$("$SCRIPT_DIR/extract-lsp-changelog.sh" "$OLD_LSP" "$NEW_LSP" 2>/dev/null || true)
fi

if [[ -n "$lsp_changelog" || ${#commits_changed[@]} -gt 0 ]]; then
  has_content=true
  output+="<h4>Changed</h4>"$'\n'
  output+="<ul>"$'\n'
  
  # Insert LSP changelog if versions changed
  if [[ -n "$lsp_changelog" ]]; then
    output+="    <li>Upgrade @sap/cds-lsp from $OLD_LSP to $NEW_LSP with the following additions and changes:"$'\n'
    output+="$lsp_changelog"$'\n'
    output+="    </li>"$'\n'
  fi
  
  if [[ ${#commits_changed[@]} -gt 0 ]]; then
    output_messages commits_changed
  fi
  output+="</ul>"$'\n'
fi

if [[ ${#commits_fixed[@]} -gt 0 ]]; then
  has_content=true
  output+="<h4>Fixed</h4>"$'\n'
  output+="<ul>"$'\n'
  output_messages commits_fixed
  output+="</ul>"$'\n'
fi

if [[ "$has_content" == "false" ]]; then
  output+="<p><em>No user-facing changes in this range.</em></p>"$'\n'
fi

# Add footer note
output+="<p><em>For details see the <a href=\"https://github.com/cap-js/cds-intellij/compare/$FROM_REF...$TO_VERSION\">full changelog</a></em></p>"$'\n'

{
  echo "changelog<<EOF"
  echo -n "$output"
  echo "EOF"
} >> "$GITHUB_OUTPUT"
