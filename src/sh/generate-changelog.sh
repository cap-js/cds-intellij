#!/bin/bash
set -eo pipefail  # Removed -u to simplify array handling

# Generate changelog HTML from conventional commits for plugin.xml
# Usage: generate-changelog.sh [from-ref] [to-ref]
#   from-ref: Start reference (default: last tag)
#   to-ref: End reference (default: HEAD)

FROM_REF="${1:-}"
TO_REF="${2:-HEAD}"

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
        # Only include user-facing chores (lsp4ij upgrades, @sap/cds-lsp upgrades)
        if [[ $message =~ (lsp4ij|cds-lsp) ]]; then
          commits_changed["$message"]=1
        fi
        ;;
      # Ignore: refactor, docs, test, build, ci (all internal/non-user-facing)
    esac
  fi
done < <(git log --oneline --no-merges --format="%s" "$FROM_REF..$TO_REF")

# Generate HTML output - only show non-empty sections
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
    
    echo "    <li>$clean_msg</li>"
  done
}

# Output sections in order: Added, Changed, Fixed
if [[ ${#commits_added[@]} -gt 0 ]]; then
  has_content=true
  echo "<h4>Added</h4>"
  echo "<ul>"
  output_messages commits_added
  echo "</ul>"
fi

if [[ ${#commits_changed[@]} -gt 0 ]]; then
  has_content=true
  echo "<h4>Changed</h4>"
  echo "<ul>"
  output_messages commits_changed
  echo "</ul>"
fi

if [[ ${#commits_fixed[@]} -gt 0 ]]; then
  has_content=true
  echo "<h4>Fixed</h4>"
  echo "<ul>"
  output_messages commits_fixed
  echo "</ul>"
fi

if [[ "$has_content" == "false" ]]; then
  echo "<p><em>No user-facing changes in this range.</em></p>"
fi

# Add footer note
echo "<p><em>For details see the <a href=\"https://github.com/cap-js/cds-intellij/compare/$FROM_REF...$TO_REF\">full changelog</a></em></p>"
