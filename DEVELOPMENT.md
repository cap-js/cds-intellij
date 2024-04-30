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

### UI development tools

The [Internal Actions UI Submenu](https://plugins.jetbrains.com/docs/intellij/internal-ui-sub.html) provides a set of tools to develop, debug, and test the plugin UI components.
You may have to enable them in the installed IDE and/or the sandbox IDE.