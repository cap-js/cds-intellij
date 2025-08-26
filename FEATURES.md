## Features

CDS Language Support for IntelliJ offers the following features based on the LSP4IJ plugin:

| Feature              | LSP4IJ Support | Server Support | LSP Request                                         | Remarks                                                                                        | Tested Working                                           |
|----------------------|----------------|:--------------:|-----------------------------------------------------|------------------------------------------------------------------------------------------------|----------------------------------------------------------|
| Syntax Highlighting  | ✔              |       ✔        | (local, based on TextMate)                          | TM Bundle is automatically registered on plugin installation (and disabled on uninstallation). | ✓                                                        |
| Code Completion      | ✔              |       ✔        | textDocument/completion                             | Completing with global identifiers supported with completionItem/resolve                       | ✓ local, ❌ global identifiers     [switch on]                       |
| Goto Definition      | ✔              |       ✔        | textDocument/definition                             |                                                                                                | ✓ *Go declaration or usages*, ❌ *Go to type declaration* |
| Goto Declaration     | ✔              |       -        | textDocument/declaration                            |                                                                                                | ✓                                                        |
| Goto Implementation  | ✔              |       ✔        | textDocument/implementation                         |                                                                                                | ✓                                                        |
| Goto Type Definition | ✔              |       -        | textDocument/typeDefinition                         |                                                                                                | ❌                                                        |
| Hover Documentation  | ✔              |       ✔        | textDocument/hover                                  |                                                                                                | ✓                                                        |
| Signature Help       | ✔              |       -        | textDocument/signatureHelp                          | Parameter hints and documentation                                                              | ❌ *(Parameter info)*                                     |
| Document Formatting  | ✔              |       ✔        | textDocument/formatting                             |                                                                                                | ✓                                                        |
| Range Formatting     | ✔              |       ✔        | textDocument/rangeFormatting                        | Format selected text ranges                                                                    | ✓                                                        |
| Diagnostics          | ✔              |       ✔        | textDocument/publishDiagnostics                     | Problems (errors, warnings).                                                                   | ✓                                                        |
| Quick Fixes          | ✔              |       ✔        | textDocument/codeAction                             |                                                                                                | ✓                                                        |
| Intention Actions    | ✔              |       –        | textDocument/codeAction                             | E.g. Refactoring or Organize Imports. No server support yet.                                   | n/a                                                      |
| Find References      | ✔              |       ✔        | textDocument/references                             |                                                                                                | ✓                                                        |
| Rename Symbol        | ✔              |       -        | textDocument/rename                                 | Symbol renaming with validation: [no server support, file rename: yes]                                                                | ❌                                                        |
| Prepare Rename       | ✔              |       ✔        | textDocument/prepareRename                          | Validate rename operation before execution                                                     | ?                                                        |
| Selection Range      | ✔              |       ✔        | textDocument/selectionRange                         | Smart selection expansion                                                                      | ❌                                                        |
| Code Folding         | ✔              |       ✔        | textDocument/foldingRange                           | Collapsible code sections                                                                      | ❌                                                        |
| Call Hierarchy       | ✔              |       ✔        | textDocument/prepareCallHierarchy + callHierarchy/* |                                                                                                | ? (sample source?)                                       |
| Type Hierarchy       | ✔              |       ✔        | textDocument/prepareTypeHierarchy + typeHierarchy/* |                                                                                                | ? (sample source?)                                       |
| Semantic Tokens      | ✔              |       ✔        | textDocument/semanticTokens                         |                                                                                                | ?                                                        |
| Document Highlights  | ✔              |       ✔        | textDocument/documentHighlight                      |                                                                                                | ✓                                                        |
| DocumentLinks        | ✔              |       ✔        | textDocument/documentLink                           |                                                                                                | ✓                                                        |
| AnalyzeDependencies  | ✔              |       ✔        |                                                     |                                                                                                | ? (how to trigger?)                                      |
| Commands             | ✔              |       ✔        |                                                     |                                                                                                | (implicitly tested)                                      |
| CodeLens             | ✔              |       ✔        | textDocument/codeLens                               |                                                                                                | ❌      [internal]                                                   |
| Outline              | ✔              |       ✔        | textDocument/documentSymbol                         | both flat and hierarchical (IJ seems to only support hierarchical)                             | ✓ (hierarchical)                                         |
| Workspace Symbols    | ✔              |       ✔        | workspace/symbol                                    | Workspace-wide symbol search                                                                   | ✓                                        |

### Examples

#### Syntax Highlighting, Code Completion, Diagnostics

![Demo of Syntax Highlighting, Code Completion, Diagnostics](.assets/syntax+completion+diagnostics.png)

#### Quick Fix

![Demo of Quick Fix](.assets/quick_fix.png)

#### Hover Documentation

![Demo of Hover Documentation](.assets/hover_documentation.png)

#### Find References

![Demo of Find References](.assets/find_references.png)

#### Outline

![Demo of Outline](.assets/outline.png)

#### Document Formatting

![Demo of Document Formatting](.assets/document_formatting.gif)

#### Adjust the Code Style

Changes in the Settings UI will synchronized with `.cdsprettier.json` in the workspace.

![Demo of Code Style Settings](.assets/code_style_settings.png)

#### Configure the CDS Language Server

Changes in the Settings UI will synchronized with `.cds-lsp/.settings.json` in the workspace.

![Demo of CDS Language Server Settings](.assets/cds_language_server_settings.png)

## Known Issues

- Maintain Translation quickfix works in principle, but properties file is not saved and thus LSP won't get updated and still suggests quickfix
- Range Formatting not correctly treating first line of selection
- Document Highlights not shown reliably
