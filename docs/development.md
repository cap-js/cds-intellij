## Development

### Sandbox IDE

#### Configuration

To configure the location of the sandbox IDE, create a file *local.properties* next to *gradle.properties*.

In this file, you can set a number of optional properties to be used by Gradle.
Note that keys should always start with `local.`. Search *build.gradle* for this prefix to find all known local settings.

Sample values for Linux:

```properties
# All properties should start with 'local.'
local.ideDir=/opt/webstorm
local.ideDirIC=/opt/intellij-community
# to copy your keymaps and various settings to the sandbox IDE on each run:
local.ideConfDir=/home/your_user/.config/JetBrains/WebStorm2025.1
local.ideConfDirIC=/home/your_user/.config/JetBrains/IdeaIC2025.1
```

#### Running the Sandbox IDE

Depending on the type of IntelliJ-based IDE you have installed locally, run one of the following commands:

- `./gradlew runWebStorm` for WebStorm
- `./gradlew runIC` for IntelliJ IDEA Community Edition

This will start the
corresponding [sandbox IDEA instance](https://plugins.jetbrains.com/docs/intellij/ide-development-instance.html) with
the plugin installed.

### Debugging the LSP Server

#### Using Locally-Modified cds-lsp

To test and debug a local version of `@sap/cds-lsp` in the plugin:

1. In the local `cds-lsp` repo:
    1. Make your code modifications.
    2. Run `npm run compile && npm pack` in the same directory.
2. In this repo:
    1. To have the `lsp/node_modules/@sap/cds-lsp` folder cleaned on each build, thus enabling local development, set
       `local.rmCdsLspNodeModules = true`
    2. Reference the path of the output `.tgz` file in the `@sap/cds-lsp` dependency in `lsp/package.json`, with a
       `file:` prefix.
    3. Run `./gradlew runIde` (or `./gradlew runWebStorm`)  to start the sandbox IDE with the modified LSP server. Each
       run will use the newest version of the `.tgz` file.

#### Debugging

When LSP debugging is [enabled](./README.md#plugin-debug-mode), the LSP server will start in debug mode,
ready for a debugger to attach.
The server is bundled but features a source map enabling you to set breakpoints in the TypeScript code.

### UI Development Tools

The [Internal Actions UI Submenu](https://plugins.jetbrains.com/docs/intellij/internal-ui-sub.html) provides a set of
tools to develop, debug, and test the plugin UI components.
You may have to enable them in the installed IDE and/or the sandbox IDE.

### Updating cds-lsp User Settings

The `lsp/schemas/user-settings.json` file defines the JSON schema for cds-lsp user settings exposed in the plugin.

#### Updating from cds-lsp

This schema is derived from the `UserSetting` enum and the `userSettingDefaults` map in cds-lsp. To update:

1. Consider changes in the corresponding source files between relevant versions

2. Update `lsp/schemas/user-settings.json` accordingly:
   - Add new settings from the `UserSetting` enum
   - Remove obsolete settings
   - Omit settings marked as internal

3. Properties to pay particular attention to:
  - `default`
  - `category`: normally derived from 2nd segment of key, but can be adjusted to include a setting in a different category
  - `group`: optionally used to group semantically related settings below a given category

#### Generating the Java Source

To generate the Java source for the user settings from the JSON schema, run:

```bash
node src/js/usersettings/patchUserSettingsJavaSrc.js
```


### Building and Testing with cds-lsp from git Branch

To build and test the plugin with a version of `@sap/cds-lsp` from a git branch, create and push a branch of this repo, changing `lsp/package.json` to read:

```json
{
  "dependencies": {
    "@sap/cds-lsp": "file:../../cds-lsp/cds-lsp-[CDS_LSP_VERSION].tgz"
  },
  "cds-lsp-branch": "[CDS_LSP_BRANCH]"
}
```
(insert the appropriate version and branch names).

You can then use the Build and Test action in the plugin repository to build and test the plugin with this version of `@sap/cds-lsp`.
