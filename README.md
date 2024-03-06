![CDS Language Support for IntelliJ](.assets/logo.png)

# CAP CDS Language Support for IntelliJ

<!--- Register repository https://api.reuse.software/register, then add REUSE badge:
[![REUSE status](https://api.reuse.software/badge/github.com/SAP-samples/REPO-NAME)](https://api.reuse.software/info/github.com/SAP-samples/REPO-NAME)
-->

## Description

Welcome to **CDS Language Support for IntelliJ** *(cds-intellij)*. It is a plugin for [IntelliJ](https://www.jetbrains.com/idea/) IDEs
that provides editing and development support for the CAP [CDS](https://cap.cloud.sap/docs/cds/) language.

### Features

Find a comprehensive list of features [here](./FEATURES.md).

![Code Completion](.assets/code_completion.png)

### Foundations

intellij-cds is based on the following components:

| Component                                                                                       | Role                                                                             | Publisher              |
|-------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------|------------------------|
| [CDS Language Server](https://www.npmjs.com/package/@sap/cds-lsp) (cds-lsp, running on Node.js) | CDS language support                                                             | SAP                    |
| CDS TextMate bundle                                                                             | CDS syntax highlighting                                                          | (shipped with cds-lsp) |
| [IntelliJ LSP API](https://plugins.jetbrains.com/docs/intellij/language-server-protocol.html)   | [LSP](https://microsoft.github.io/language-server-protocol/) support in IntelliJ | JetBrains              |

#### Terms

- Language servers: provide language-specific features like code completion, diagnostics, etc.
- Language Server Protocol (LSP): common protocol for communication between IDEs and language servers.


## Requirements

### Supported IDEs
The intellij-cds plugin is compatible with the latest **paid** IntelliJ IDEs including IDEA Ultimate and WebStorm (due to the restricted availability of JetBrains' LSP API).

### Operating Systems
Tested on Windows, macOS, and Linux.


## Download and Installation

Coming soon…


## How to obtain support
[Create an issue](https://github.com/cap-js/cds-intellij/issues) in this repository if you find a bug or have questions about the content.

For additional support, [ask a question in SAP Community](https://answers.sap.com/questions/ask.html).

### Known Issues

🚫 CDS Language Support for IntelliJ is not compatible with non-commercial IntelliJ IDEs, such as IntelliJ Community Edition. This is due to the fact that intellij-cds relies on the IntelliJ LSP API, which is only available in commercial IDEs.

### Logs

To aid support, please include relevant log files in your report.
Depending on the kind of problem encountered, you may want to include the logs from the **IDE**, the **LSP client** and/or the **LSP server**.

#### IDE Logs
Logs produced by the IDE can be found by opening the *Help* menu and selecting *Show Log in <platform-dependent tool>*.
See [here](https://intellij-support.jetbrains.com/hc/en-us/articles/207241085-Locating-IDE-log-files) for more information.

#### LSP Logs
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

After restarting the IDE, find the logs in the [plugin directory](https://intellij-support.jetbrains.com/hc/en-us/articles/206544519-Directories-used-by-the-IDE-to-store-settings-caches-plugins-and-logs),
subdirectory `lib/cds-lsp`, file `stdio.log`.

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
