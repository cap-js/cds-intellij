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
| [LSP4IJ extension](https://plugins.jetbrains.com/plugin/23257-lsp4ij)                           | [LSP](https://microsoft.github.io/language-server-protocol/) support in IntelliJ | Red Hat                |


### Known Issues

#### Installation and Compatibility

| Symptom                                                                                        | Solution                                                                                                                                                                                                           |
|------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| SAP CDS Language Support for IntelliJ is not enabled in the IDE.                               | 1.x versions of the plugin are not compatible with the **free** IntelliJ **Community** Edition. Make sure you are using a version ≥ 2.x of the plugin.                                                             |
| SAP CDS Language Support for IntelliJ version 7 or 8 cannot be updated to the current version. | The plugin version has been reset to 1.0.0 for the first JetBrains Marketplace release. Uninstall the previous version of the plugin. Install the latest version from the JetBrains Marketplace.                   |
| CDS file icons are missing and/or no syntax highlighting                                       | Uninstall any previous versions of the plugin *(File > Settings > Plugins)*. Remove the 'cds' TextMate Bundle *(File > Settings > Editor > TextMate Bundles)*. Restart the IDE. Install the latest plugin version. |

### Logs

To aid support, please include relevant log files in your report.
Depending on the kind of problem encountered, you may want to include the logs from the **IDE**, the **LSP client** and/or the **LSP server**.

#### IDE Logs

Logs produced by the IDE can be found by opening the _Help_ menu and selecting *Show Log in <platform-dependent tool>*.
See [Locating IDE log files](https://intellij-support.jetbrains.com/hc/en-us/articles/207241085-Locating-IDE-log-files) for more information.

#### Language Server Protocol (LSP) Logs

Logging of the stdio communication between the LSP client and server (i.e., the protocol messages) can be activated by modifying your IDE's `vmoptions` file:
- Edit the file specific to your IntelliJ installation by opening the IDE and going to *Help > Edit Custom VM Options...*.
- Add the following line:
```
-DDEBUG=cds-lsp
```

Alternatively, set the following environment variable for your IDE:
```
DEBUG=cds-lsp
```

These settings will also enable debug logging for the LSP server.

After restarting the IDE, find the logs in the [plugin directory](https://intellij-support.jetbrains.com/hc/en-us/articles/206544519-Directories-used-by-the-IDE-to-store-settings-caches-plugins-and-logs), at `lib/cds-lsp/stdio.json`.

#### LSP Client and Plugin Logs

To include debug logs produced by the LSP client (part of the IDE) and the SAP CDS Language Support for IntelliJ plugin, you need to enable the corresponding settings in the IDE:
- Open the _Help_ menu and select _Diagnostic Tools > Debug Log Settings…_.
- In the dialog that opens, add the following lines (omit sub-categories if not needed):
```
com.intellij.platform.lsp
cds-intellij
cds-intellij/TextMate Bundle
cds-intellij/Code Style
```
- Click _OK_ to save the settings.

#### LSP Server Logs

The LSP server logs to its own file, which you can locate by opening your system temporary directory, then the sub-folder `cdxlsp`, and finally heading to the file most recently modified at the time of the reported problem.

Hint: depending on your operating system, the temporary directory may be at one of the following locations:

| OS      | Default location                         | Environment variables | Command                            |
|---------|------------------------------------------|-----------------------|------------------------------------|
| Windows | C:\Users\\<username\>\AppData\Local\Temp | %TEMP%                |                                    |
| macOS   | n/a                                      | $TMPDIR               | node -e "console.log(os.tmpdir())" |
| Linux   | /tmp                                     | $TMPDIR               | node -e "console.log(os.tmpdir())" |


## Contributing

If you wish to contribute code, offer fixes or improvements, please send a pull request. Due to legal reasons, contributors will be asked to accept a DCO when they create the first pull request to this project. This happens in an automated fashion during the submission process. SAP uses [the standard DCO text of the Linux Foundation](https://developercertificate.org/).


## License

Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved. This project is licensed under the Apache Software License, version 2.0 except as noted otherwise in the [LICENSE](LICENSE) file.

Copyright © 2024 JetBrains s.r.o. IntelliJ IDEA and the IntelliJ IDEA logo are registered trademarks of JetBrains s.r.o.
