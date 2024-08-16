![CDS Language Support for IntelliJ](.assets/logo.png) [![IntelliJ](.assets/IntelliJ_IDEA_icon.svg)](https://www.jetbrains.com/)

# CAP CDS Language Support for IntelliJ

[![REUSE status](https://api.reuse.software/badge/github.com/cap-js/cds-intellij)](https://api.reuse.software/info/github.com/cap-js/cds-intellij)
![Main build passing](https://github.com/cap-js/cds-intellij/actions/workflows/main.yml/badge.svg)
![Release build passing](https://github.com/cap-js/cds-intellij/actions/workflows/release.yml/badge.svg)

## Description

Welcome to **CDS Language Support for IntelliJ**. It is a plugin for [IntelliJ](https://www.jetbrains.com/idea/) IDEs
that provides editing and development support for the CAP [CDS](https://cap.cloud.sap/docs/cds/) language.

### Features

Look at our [comprehensive list of features](./FEATURES.md).

![Code Completion](.assets/syntax+completion+diagnostics.png)

## Requirements

### Supported IDEs

This plugin is compatible with the latest **paid** IntelliJ IDEs including IDEA Ultimate and WebStorm.  Check https://www.jetbrains.com/ which option is best for you.

> The LSP API is only available in the commercial versions JetBrains' IDEs, which is why the plugin doesn't run in the free variants.

### Operating Systems

Tested on Windows, macOS, and Linux.


## Download and Installation

### As a Zip File

Prepare your environment:

1. Install [Node.js](https://nodejs.org/en/) on your computer.
2. Make sure that your IntelliJ IDEA Ultimate (or other commercial IntelliJ IDE) runs with a `PATH` that includes the Node.js executable.

Install or update the plugin:

1. Download the latest [release](https://github.com/cap-js/cds-intellij/releases) from GitHub.
2. In IntelliJ, go to `File > Settings > Plugins > ⚙ > Install Plugin from Disk…` and select the downloaded .zip file.

### From JetBrains Marketplace

Coming soon. We are working on it.


## How to Obtain Support

[Create an issue](https://github.com/cap-js/cds-intellij/issues) in this repository if you find a bug or have questions about the content.

For additional support, [ask a question in SAP Community](https://answers.sap.com/questions/ask.html).

#### Terms

- Language servers: provide language-specific features like code completion, diagnostics, etc.
- Language Server Protocol (LSP): common protocol for communication between IDEs and language servers.

cds-intellij is based on the following components:

| Component                                                                                       | Role                                                                             | Publisher              |
|-------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------|------------------------|
| [CDS Language Server](https://www.npmjs.com/package/@sap/cds-lsp) (cds-lsp, running on Node.js) | CDS language support                                                             | SAP                    |
| CDS TextMate bundle                                                                             | CDS syntax highlighting                                                          | (shipped with cds-lsp) |
| [IntelliJ LSP API](https://plugins.jetbrains.com/docs/intellij/language-server-protocol.html)   | [LSP](https://microsoft.github.io/language-server-protocol/) support in IntelliJ | JetBrains              |


### Known Issues

#### Installation and Compatibility

| Symptom                                                      | Solution                                                                                                                                                                                                           |
|--------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| CDS Language Support for IntelliJ is not enabled in the IDE. | 🚫 The plugin is not compatible with the **free** IntelliJ **Community** Edition. Make sure you are using a commercial IntelliJ IDE such as IDEA Ultimate or WebStorm.                                             |
| CDS file icons are missing and/or no syntax highlighting     | Uninstall any previous versions of the plugin *(File > Settings > Plugins)*. Remove the 'cds' TextMate Bundle *(File > Settings > Editor > TextMate Bundles)*. Restart the IDE. Install the latest plugin version. |

### Logs

To aid support, please include relevant log files in your report.
Depending on the kind of problem encountered, you may want to include the logs from the **IDE**, the **LSP client** and/or the **LSP server**.

#### IDE Logs

Logs produced by the IDE can be found by opening the *Help* menu and selecting *Show Log in <platform-dependent tool>*.
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

#### LSP Client Logs

To include the logs produced by the LSP client (part of the IDE), you need to enable the corresponding setting in the IDE:
- Open the *Help* menu and select *Diagnostic Tools > Debug Log Settings…*.
- In the dialog that opens, add the following line:
```
#com.intellij.platform.lsp
```
- Click *OK* to save the settings.

#### LSP Server Logs

The LSP server logs to its own file, which you can locate by opening your system temporary directory, then the sub-folder `cdxlsp`, and finally heading to the file most recently modified at the time of the reported problem.

Hint: depending on your operating system, the temporary directory may be at one of the following locations:

| OS           | Default location                        | Environment variables | Command                            |
|--------------|-----------------------------------------|-----------------------|------------------------------------|
| Windows      | C:\Users\\[username]\AppData\Local\Temp | %TEMP%                |                                    |
| macOS, Linux | /tmp                                    | $TMPDIR               | node -e "console.log(os.tmpdir())" |


## Contributing

If you wish to contribute code, offer fixes or improvements, please send a pull request. Due to legal reasons, contributors will be asked to accept a DCO when they create the first pull request to this project. This happens in an automated fashion during the submission process. SAP uses [the standard DCO text of the Linux Foundation](https://developercertificate.org/).


## License

Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved. This project is licensed under the Apache Software License, version 2.0 except as noted otherwise in the [LICENSE](LICENSE) file.

Copyright © 2024 JetBrains s.r.o. IntelliJ IDEA and the IntelliJ IDEA logo are registered trademarks of JetBrains s.r.o.