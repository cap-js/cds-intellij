## Development

### Sandbox IDE

Run `./gradlew runIde` to fire up a [sandbox IDE](https://plugins.jetbrains.com/docs/intellij/ide-development-instance.html) with the plugin installed.

#### Synchronize local IDE settings

In order to use your local IDE settings (such as hotkeys or themes) with the sandbox IDE:
1. Create a file *local.properties* next to *gradle.properties*.
2. Write the following line to the file:
   `local.ideConfDir = /path/to/config/dir/e.g./WebStorm2024.1`

From now on, settings (keymaps and various options) will be copied from the local configuration dir to that of the sandbox IDE on each run.

### Local Gradle settings

In *local.properties*, you can set a number of properties to be used by Gradle.
Note that keys should always start with `local.`. Search in *build.gradle* for this prefix to find all known local settings.

Any such properties are optional. Remove or comment out an existing entry to disable the corresponding feature.

### Debugging the LSP Server

#### Using locally-modified cds-lsp

To test and debug a local version of `@sap/cds-lsp` in the plugin:
1. In the local `cds-lsp` repo:
   1. Make your code modifications.
   2. Run `npm run compile && npm pack` in the same directory.
2. In this plugin:
   1. Add the following line to `local.properties`:
      `local.cdsLspFromTar = true`
   2. Reference the path of the output `.tgz` file in the `@sap/cds-lsp` dependency in `lsp/package.json`, with a `file:` prefix.
   3. Run `./gradlew runIde` to start the sandbox IDE with the modified LSP server. Each run will use the newest version of the `.tgz` file.

#### Debugging

When LSP debugging is [enabled](./README.md#language-server-protocol-lsp-logs), the LSP server will start in debug mode, ready for a debugger to attach.
The server is bundled but features a source map enabling you to set breakpoints in the TypeScript code. 

### UI development tools

The [Internal Actions UI Submenu](https://plugins.jetbrains.com/docs/intellij/internal-ui-sub.html) provides a set of tools to develop, debug, and test the plugin UI components.
You may have to enable them in the installed IDE and/or the sandbox IDE.