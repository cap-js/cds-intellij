![SAP CDS Language Support for IntelliJ](.assets/logo.png) [![IntelliJ](.assets/IntelliJ_IDEA_icon.svg)](https://www.jetbrains.com/)

# SAP CDS Language Support for IntelliJ

[![REUSE status](https://api.reuse.software/badge/github.com/cap-js/cds-intellij)](https://api.reuse.software/info/github.com/cap-js/cds-intellij)
[![Main build passing](https://github.com/cap-js/cds-intellij/actions/workflows/main.yml/badge.svg)](https://github.com/cap-js/cds-intellij/actions/workflows/main.yml)
[![Release build passing](https://github.com/cap-js/cds-intellij/actions/workflows/release.yml/badge.svg)](https://github.com/cap-js/cds-intellij/actions/workflows/release.yml)
![IntelliJ Version](https://img.shields.io/jetbrains/plugin/v/com.sap.cap.cds)
<!-- ![IntelliJ Downloads](https://img.shields.io/jetbrains/plugin/d/com.sap.cap.cds) -->

## Description

Welcome to **SAP CDS Language Support for IntelliJ**. It is a plugin for [IntelliJ](https://www.jetbrains.com/idea/) IDEs
that provides editing and development support for the [CDS](https://cap.cloud.sap/docs/cds/) language as used in the SAP Cloud Application Programming Model (CAP).

### Features

Look at our [comprehensive list of features](./FEATURES.md).

![Code Completion](.assets/syntax+completion+diagnostics.png)

## Requirements

### Supported IDEs

This plugin is compatible with a [variety of IntelliJ IDEs](https://plugins.jetbrains.com/plugin/25209-sap-cds-language-support), **paid** and **community** editions, including IDEA and WebStorm.  Check https://www.jetbrains.com/ to find out which option is best for you.

### Operating Systems

Tested on Windows, macOS, and Linux.


## Download and Installation

Prepare your environment:

1. Install [![required Node.js version](https://img.shields.io/badge/dynamic/json?url=https%3A%2F%2Fwww.unpkg.com%2F%40sap%2Fcds-lsp%2Fpackage.json&query=%24.engines.node&label=Node.js&cacheSeconds=3600)](https://nodejs.org/en/)
   on your computer if not already present.
2. Register the new Node.js installation in your IntelliJ IDE by going to *File > Settings > Languages & Frameworks > CDS*.
3. If you have installed the SAP CDS Language Support for IntelliJ plugin from a ZIP file before, uninstall it.
     - Note: we have reset the current plugin version to `1.0.0` for the first JetBrains Marketplace release, to clarify that the feature set is still incomplete.

Install or update the plugin:

1. In your IntelliJ IDE, go to *File > Settings > Plugins*.
2. Click the _Marketplace_ tab.
3. Search for "SAP CDS Language Support" and click *Install*.

**Note:** Support for **_Community_** editions of IntelliJ IDEs is currently in **beta** stage and requires manual installation
from [GitHub](https://github.com/cap-js/cds-intellij/releases). Tap `Shift` **twice**, then select _Install Plugin from Disk..._


## How to Obtain Support

[Create an issue](https://github.com/cap-js/cds-intellij/issues) in this repository if you find a bug or have questions about the content.

For additional support, [ask a question in SAP Community](https://answers.sap.com/questions/ask.html).

#### Terms

- Language servers: provide language-specific features like code completion, diagnostics, etc.
- Language Server Protocol (LSP): common protocol for communication between IDEs and language servers.

SAP CDS Language Support for IntelliJ is based on the following components:

| Component                                                                                       | Role                                                                             | Publisher              |
|-------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------|------------------------|
| [CDS Language Server](https://www.npmjs.com/package/@sap/cds-lsp) (cds-lsp, running on Node.js) | SAP CDS Language Support                                                         | SAP                    |
| CDS TextMate bundle                                                                             | CDS syntax highlighting                                                          | (shipped with cds-lsp) |
| [LSP4IJ plugin](https://plugins.jetbrains.com/plugin/23257-lsp4ij)                              | [LSP](https://microsoft.github.io/language-server-protocol/) support in IntelliJ | Red Hat                |


### Known Compatibility Issues

| Symptom                                                                                        | Solution                                                                                                                                                                                                           |
|------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| SAP CDS Language Support for IntelliJ is not enabled in the IDE.                               | 1.x versions of the plugin are not compatible with the **free** IntelliJ **Community** Edition. Make sure you are using a version ≥ 2.x of the plugin.                                                             |
| CDS file icons are missing and/or no syntax highlighting                                       | Uninstall any previous versions of the plugin *(File > Settings > Plugins)*. Remove the 'cds' TextMate Bundle *(File > Settings > Editor > TextMate Bundles)*. Restart the IDE. Install the latest plugin version. |

### Logs

To aid support, please include relevant log files in your report.
Depending on the kind of problem encountered, you may want to include the logs from the **IDE**, the **LSP4IJ plugin**, the **CDS Language Support Plugin**, the **LSP client–server (stdio) communication**, and/or the **LSP server**.

#### IDE Logs

Logs produced by the IDE can be found by opening the _Help_ menu and selecting *Show Log in <platform-dependent tool>*.
See [Locating IDE log files](https://intellij-support.jetbrains.com/hc/en-us/articles/207241085-Locating-IDE-log-files) for more information.

#### LSP4IJ Plugin Logs and Stdio Communication

To include any errors occurring in the LSP4IJ Plugin in the IDE logs, click *View > Tool Windows > Language Servers*. Click on *CDS Language Server*. On the right-hand side, open the *Debug* tab on the right and select *Error reporting: In log*. After selecting *Trace: Verbose*, you'll find the stdio communication after clicking on the language-server instance (below 'CDS Language Server' on the left) and opening the *Traces* tab on the right.

#### CDS Language Support Plugin Logs

To include debug logs produced by the SAP CDS Language Support for IntelliJ plugin, you need to enable the corresponding settings in the IDE:
- Open the _Help_ menu and select _Diagnostic Tools > Debug Log Settings…_.
- In the dialog that opens, add the following lines (omit sub-categories if not needed):
```
cds-intellij
cds-intellij/TextMate Bundle
cds-intellij/Code Style
cds-intellij/Language Server
```
- Click _OK_ to save the settings.

#### Plugin Debug Mode

To enable further logging and debugging, enable **plugin debug mode** by setting the following environment variable for your IDE by modifying your IDE's `vmoptions` file:
- Edit the file specific to your IntelliJ installation by opening the IDE and going to *Help > Edit Custom VM Options...*.
- Add the following line:
```
-DDEBUG=cds-lsp
```

Alternatively, set the following environment variable for your IDE:
```
DEBUG=cds-lsp
```

Restart the IDE to apply the changes.

This setting will enable:
- logging of the **stdio communication** between the LSP client and server (i.e., the LSP messages),
- **tracing** for the LSP server (see below),
- Node.js **debugging** for the LSP server, allowing you to attach a debugger.

##### Stdio Logs

Open the stdio logs by going to *Tools > CDS > LSP Stdio Logs > Show Log File* in your IDE. Alternatively, copy the file path by going to *Tools > CDS > LSP Stdio Logs > Copy Log File Path*.

Alternatively, find the log file in the [plugin directory](https://intellij-support.jetbrains.com/hc/en-us/articles/206544519-Directories-used-by-the-IDE-to-store-settings-caches-plugins-and-logs), at `lib/cds-lsp/stdio.json`.

##### LSP Server Logs and Tracing

Open the LSP server logs by going to *Tools > CDS > LSP Server Logs > Show Log File* in your IDE. Alternatively, copy the file path by going to *Tools > CDS > LSP Server Logs > Copy Log File Path*.

Alternatively, find the log file in your system temporary directory, sub-folder `cdxlsp`.

###### Local Logs

Alternatively, if your project has a subfolder `.cds-lsp` with a file `.settings.json` inside, the LSP server will log to that folder instead.

###### Trace Level

LSP-server tracing is set to level `verbose` by default in _plugin debug mode_.

To change verbosity, add the setting `CDS_LSP_TRACE_COMPONENTS=*:debug` to the environment variables for the LSP server by going to *File > Settings > Languages & Frameworks > CDS* in your IDE and adding it to the _Additional env for LSP server_ field (separate multiple env variables with a semicolon). Restart the IDE to apply the changes.

Available trace levels are `infrastructure`, `error`, `warning`, `info`, `verbose`, and `debug` (ascending verbosity).
To disable tracing, set `CDS_LSP_TRACE_COMPONENTS=` (empty value).

Note that for large projects, more verbose tracing may significantly inflate logs and negatively impact performance. Use it with care.


## Contributing

If you wish to contribute code, offer fixes or improvements, please send a pull request. Due to legal reasons, contributors will be asked to accept a DCO when they create the first pull request to this project. This happens in an automated fashion during the submission process. SAP uses [the standard DCO text of the Linux Foundation](https://developercertificate.org/).


## License

Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved. This project is licensed under the Apache Software License, version 2.0 except as noted otherwise in the [LICENSE](LICENSE) file.

Copyright © 2024 JetBrains s.r.o. IntelliJ IDEA and the IntelliJ IDEA logo are registered trademarks of JetBrains s.r.o.
