## Features

CDS Language Support for IntelliJ offers the following features based on the LSP4IJ plugin:

| Feature                  | LSP4IJ Support | API Support | Server Support | LSP Request                     | Remarks                                                                                        |
|--------------------------|----------------|:-----------:|:--------------:|---------------------------------|------------------------------------------------------------------------------------------------|
| Syntax Highlighting      |                |     ✔      |       ✔        | (local, based on TextMate)      | TM Bundle is automatically registered on plugin installation (and disabled on uninstallation). |
| Code Completion          |                |     ✔      |       ✔        | textDocument/completion         | Completing with global identifiers supported with completionItem/resolve (2024.2)              |
| Goto Definition          |                |     ✔      |       ✔        | textDocument/definition         |                                                                                                |
| Hover Documentation      |                |     ✔      |       ✔        | textDocument/hover              |                                                                                                |
| Document Formatting      |                |     ✔      |       ✔        | textDocument/formatting         |                                                                                                |
| Diagnostics              |                |     ✔      |       ✔        | textDocument/publishDiagnostics | Problems (errors, warnings).                                                                   |
| Quick Fixes              |                |     ✔      |       ✔        | textDocument/codeAction         | Translation support only in 2.x                                                                |
| Intention Actions        |                |     ✔      |       –        | textDocument/codeAction         | E.g. Refactoring or Organize Imports. No server support yet.                                   |
| Find References          |                |     ✔      |       ✔        | textDocument/references         |                                                                                                |
| Semantic Tokens          |                |     ✔      |       ✔        | textDocument/semanticTokens     | Improved highlighting: server dynamically assigns token semantics.                             |
| Document Highlights etc. |                |     –      |       ✔        | (various)                       |                                                                                                |
| DocumentLinks            |                |     –      |       ✔        |                                 |                                                                                                |
| AnalyzeDependencies      |                |     –      |       ✔        |                                 |                                                                                                |
| Commands                 |                |     –      |       ✔        |                                 |                                                                                                |
| - Restart LSP            |                |     –      |                | via short cut                   |                                                                                                |
| CodeLens                 |                |     ?      |       ✔        |                                 |                                                                                                |
| Outline                  |                |     ?      |       ✔        | textDocument/symbols            | both flat and hierarchical                                                                     |

Known issues/open points:

- Workspace Symbols: managed (common.cds) not shown in cap-cloud-samples repo
- `.cdsprettier.json` schema registration needed to support code completion in text editor
- Maintain translation quickfix works in principal, but properties file is not saved and thus LSP won't get updated and
  still suggest quickfix
- Change serverId to cap-cds-language-server

### Examples

#### Syntax Highlighting, Code Completion, Diagnostics

![Demo of Syntax Highlighting, Code Completion, Diagnostics](.assets/syntax+completion+diagnostics.png)

#### Quick Fix

![Demo of Quick Fix](.assets/quick_fix.png)

#### Hover Documentation

![Demo of Hover Documentation](.assets/hover_documentation.png)

#### Find References

![Demo of Find References](.assets/find_references.png)

#### Document Formatting: before…

![Demo of Document Formatting (before)](.assets/document_formatting1.png)

#### … and after

![Demo of Document Formatting (after)](.assets/document_formatting2.png)

#### Adjust the Code Style

Changes in the Settings UI will synchronized with `.cdsprettier.json` in the workspace.

![Demo of Code Style Settings](.assets/code_style_settings.png)

#### Configure the CDS Language Server

Changes in the Settings UI will synchronized with `.cds-lsp/.settings.json` in the workspace.

![Demo of CDS Language Server Settings](.assets/cds_language_server_settings.png)

### Upcoming

Depending on the LSP4IJ development roadmap, we expect to see more features available in the near future.
These will enable *SAP CDS Language Support for IntelliJ* to provide:

- Type generation with [CDS Typer](https://cap.cloud.sap/docs/tools/cds-typer) on save
- and more…
