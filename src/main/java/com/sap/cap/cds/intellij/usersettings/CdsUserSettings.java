package com.sap.cap.cds.intellij.usersettings;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service(Service.Level.PROJECT)
public class CdsUserSettings {

    public static final String USER_SETTINGS_JSON = ".cds-lsp/.settings.json";
    private Map<String, Object> settings;

    public CdsUserSettings(Project project) {
    }

    public static CdsUserSettings getInstance(Project project) {
        return project.getService(CdsUserSettings.class);
    }

    // Note: method body is generated
    public static String getLabel(String settingKey) {
        return switch (settingKey) {
            case "cds.codeLensStatistics.enabled" -> "Code Lens Statistics";
            case "cds.compiler.markMissingI18nDefault" -> "Mark Missing I18n Default";
            case "cds.compiler.showInternalErrors" -> "Show Internal Errors";
            case "cds.completion.annotations.omitRedundantTypesInSnippets" -> "Omit Redundant Types in Snippets";
            case "cds.completion.docFiles" -> "Documentation Files";
            case "cds.completion.formatSnippets" -> "Format Snippets";
            case "cds.completion.showDocumentation" -> "Show Documentation";
            case "cds.completion.workspaceSymbols.maxProposals" -> "Max Proposals";
            case "cds.completion.workspaceSymbols.minPrefixLength" -> "Min Prefix Length";
            case "cds.completion.workspaceSymbols.useShortname" -> "Use Short Name";
            case "cds.contributions.enablement.additionalAnalyticalAnnotations" -> "Additional Analytical Annotations";
            case "cds.contributions.enablement.odata" -> "OData";
            case "cds.contributions.features.completion" -> "Completion Support";
            case "cds.contributions.features.diagnostics" -> "Diagnostics Support";
            case "cds.contributions.features.hover" -> "Hover Support";
            case "cds.contributions.features.index" -> "Index Support";
            case "cds.contributions.features.quickFixes" -> "Quick Fixes Support";
            case "cds.contributions.features.semanticHighlighting" -> "Semantic Highlighting Support";
            case "cds.contributions.registry" -> "Registry";
            case "cds.diagnosticsSeverity" -> "Severity Level";
            case "cds.outline.elements.associationComposition" -> "Show Association/Composition";
            case "cds.outline.semantical" -> "Semantical";
            case "cds.quickfix.importArtifact" -> "Import Artifact";
            case "cds.refactoring.files.delete.enabled" -> "Update Usings on Delete";
            case "cds.refactoring.files.rename.enabled" -> "Update Usings on Rename";
            case "cds.semanticHighlighting.enabled" -> "Semantic Highlighting";
            case "cds.semanticHighlighting.odata.enabled" -> "OData Semantic Highlighting";
            case "cds.typeGenerator.command" -> "Command";
            case "cds.typeGenerator.enabled" -> "Enabled";
            case "cds.typeGenerator.localInstallationOnly" -> "Local Installation Only";
            case "cds.typeGenerator.outputPath" -> "Output Path";
            case "cds.whereused.showGenericAnnotations" -> "Show Generic Annotations";
            case "cds.whereused.showStringConstants" -> "Show String Literals";
            case "cds.workspace.persistency.enabled" -> "Persistency Enabled";
            case "cds.workspace.persistency.garbageCollectOrphanedIndexesAfterNSaves" -> "Garbage Collect After N Saves";
            case "cds.workspace.persistency.garbageCollectOrphanedIndexesAfterStartup" -> "Garbage Collect After Startup";
            case "cds.workspace.persistency.indexAllAfterStartup" -> "Index All After Startup";
            case "cds.workspace.persistency.persistAfterCompile" -> "Persist After Compile";
            case "cds.workspace.persistency.persistAfterSave" -> "Persist After Save";
            case "cds.workspace.persistency.reindexAfterCompileIfRestored" -> "Reindex After Compile If Restored";
            case "cds.workspace.persistency.restoreAfterStartup" -> "Restore After Startup";
            case "cds.workspace.persistency.restoreBeforeCompile" -> "Restore Before Compile";
            case "cds.workspace.scanCsn" -> "Scan CSN Files";
            case "cds.workspace.scanDependentModules" -> "Scan Dependent Modules";
            case "cds.workspaceSymbols.caseInsensitive" -> "Case-Insensitive Symbols";
            case "cds.workspaceSymbols.lazy" -> "Lazy Load Symbols";
            case "cds.workspaceValidationMode" -> "Validation Mode";
            default -> null;
        };
    }

    // Note: method body is generated
    public static String getDescription(String settingKey) {
        return switch (settingKey) {
            case "cds.compiler.markMissingI18nDefault" -> "Show warning problem markers for unresolved `i18n` references.\n\n**NOTE:** For huge models it may show performance degradations";
            case "cds.compiler.showInternalErrors" -> "Print internal compiler errors to the console";
            case "cds.completion.annotations.omitRedundantTypesInSnippets" -> "Omit redundant record types in suggested snippets";
            case "cds.completion.docFiles" -> "Potential names of files to show as documentation. This is an ordered list. The first filename that exists is used.";
            case "cds.completion.formatSnippets" -> "Format snippets after applying in code completion";
            case "cds.completion.showDocumentation" -> "Show documentation in code completion";
            case "cds.completion.workspaceSymbols.maxProposals" -> "Maximum number of workspace symbols to suggest. Default is -1 (all)";
            case "cds.completion.workspaceSymbols.minPrefixLength" -> "Number of characters required to suggest (global) workspace symbols. Default is -1 (switched off - the feature may delay completion)";
            case "cds.completion.workspaceSymbols.useShortname" -> "Use short names for workspace symbols in completion";
            case "cds.contributions.enablement.additionalAnalyticalAnnotations" -> "Provide support for additional annotations used CAP Embedded Analytics\n\nSee [capire](https://cap.cloud.sap/docs) for details";
            case "cds.contributions.enablement.odata" -> "Provide extended annotation support for OData";
            case "cds.contributions.features.completion" -> "Enable completion support from contributions";
            case "cds.contributions.features.diagnostics" -> "Enable diagnostics support from contributions";
            case "cds.contributions.features.hover" -> "Enable hover support from contributions";
            case "cds.contributions.features.index" -> "Enable index support from contributions";
            case "cds.contributions.features.quickFixes" -> "Enable quick fixes support from contributions";
            case "cds.contributions.features.semanticHighlighting" -> "Enable semantic highlighting support from contributions";
            case "cds.contributions.registry" -> "NPM registry to be used for installation/update of contributions (e.g. OData annotation support)";
            case "cds.diagnosticsSeverity" -> "Minimum severity of compiler messages to show in _Problems_ view";
            case "cds.outline.semantical" -> "Show a semantical outline structure as opposed to the (default) flat list";
            case "cds.quickfix.importArtifact" -> "Provide a _quick fix_ for artifacts not yet imported (default disabled - the feature is CPU intensive)";
            case "cds.refactoring.files.delete.enabled" -> "Adapt `using` statements in other files when deleting CDS files";
            case "cds.refactoring.files.rename.enabled" -> "Adapt `using` statements in other files when renaming CDS files";
            case "cds.semanticHighlighting.enabled" -> "Semantic highlighting of certain identifiers";
            case "cds.semanticHighlighting.odata.enabled" -> "Semantic highlighting for OData annotations";
            case "cds.typeGenerator.command" -> "The command that is executed when generating model types. You can use the following variables in your command template:\n\n- `${typerBinary}`: the binary\n- `${targetFile}`: the file that is being typed\n- `${outputDirectory}`: the directory into which the type information is generated";
            case "cds.typeGenerator.enabled" -> "Generate type definitions.\nRequires `@cap-js/cds-typer` to be installed\n\n**NOTE:** This feature is still experimental (beta)";
            case "cds.typeGenerator.outputPath" -> "Directory that serves as root for the generated type definitions. Relative to the project's root";
            case "cds.whereused.showGenericAnnotations" -> "Find usages of same annotation names via _References_ command and explicit annotation definitions via _Definition_ command";
            case "cds.whereused.showStringConstants" -> "Find same string constants via _References_ command";
            case "cds.workspace.persistency.enabled" -> "Enable workspace index persistency";
            case "cds.workspace.persistency.garbageCollectOrphanedIndexesAfterNSaves" -> "Garbage collect orphaned indexes after N saves (0 = disabled)";
            case "cds.workspace.persistency.garbageCollectOrphanedIndexesAfterStartup" -> "Garbage collect orphaned indexes after startup";
            case "cds.workspace.persistency.indexAllAfterStartup" -> "Index all files after startup";
            case "cds.workspace.persistency.persistAfterCompile" -> "Persist workspace index after compilation";
            case "cds.workspace.persistency.persistAfterSave" -> "Persist workspace index after save";
            case "cds.workspace.persistency.reindexAfterCompileIfRestored" -> "Reindex workspace after compilation if index was restored";
            case "cds.workspace.persistency.restoreAfterStartup" -> "Restore workspace index after startup";
            case "cds.workspace.persistency.restoreBeforeCompile" -> "Restore workspace index before compilation";
            case "cds.workspace.scanCsn" -> "How CSN files are detected:\n- `ByFileExtension` (default): CSN files (.csn or .csn.json) will be included in validation and workspace symbols\n- `InspectJson`: additionally looks into .json files if they are valid CSN. This will take considerably more time at scanning\n- `Off`: will not scan for CSN files at all";
            case "cds.workspace.scanDependentModules" -> "Scan first level dependencies in node_modules. This may increase start-up time. Enable if you want code completions for global identifiers and import paths, or want to see definitions of dependencies in workspace symbols";
            case "cds.workspaceSymbols.caseInsensitive" -> "_Workspace Symbols_ will show symbols containing the given query, independent of character casings";
            case "cds.workspaceSymbols.lazy" -> "_Workspace Symbols_ will show symbols that have the queried characters in the right order, but not necessarily consecutive";
            case "cds.workspaceValidationMode" -> "Specify which CDS files are validated:\n- `ActiveEditorOnly` (default): only the active editor is validated\n- `OpenEditorsOnly` additionally keeps other open editors' validation up-to-date";
            default -> null;
        };
    }

    // Note: method body is generated
    public static boolean hasEnumValues(String settingKey) {
        return getEnumValues(settingKey) != null;
    }

    // Note: method body is generated
    public static String[] getEnumValues(String settingKey) {
        return switch (settingKey) {
            case "cds.diagnosticsSeverity" -> new String[]{"Error", "Warning", "Info", "Debug"};
            case "cds.workspace.scanCsn" -> new String[]{"Off", "ByFileExtension", "InspectJson"};
            case "cds.workspaceValidationMode" -> new String[]{"ActiveEditorOnly", "OpenEditorsOnly"};
            default -> null;
        };
    }

    public Map<String, Object> getSettings() {
        if (settings == null) {
            settings = new HashMap<>(getDefaults());
        }
        return settings;
    }

    private static final Map<String, Object> DEFAULTS;

    // Note: static initializer is generated
    static {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("cds.codeLensStatistics.enabled", false);
        defaults.put("cds.compiler.markMissingI18nDefault", false);
        defaults.put("cds.compiler.showInternalErrors", true);
        defaults.put("cds.completion.annotations.omitRedundantTypesInSnippets", false);
        defaults.put("cds.completion.docFiles", "README.md");
        defaults.put("cds.completion.formatSnippets", false);
        defaults.put("cds.completion.showDocumentation", true);
        defaults.put("cds.completion.workspaceSymbols.maxProposals", -1);
        defaults.put("cds.completion.workspaceSymbols.minPrefixLength", -1);
        defaults.put("cds.completion.workspaceSymbols.useShortname", false);
        defaults.put("cds.contributions.enablement.additionalAnalyticalAnnotations", false);
        defaults.put("cds.contributions.enablement.odata", false);
        defaults.put("cds.contributions.features.completion", false);
        defaults.put("cds.contributions.features.diagnostics", false);
        defaults.put("cds.contributions.features.hover", false);
        defaults.put("cds.contributions.features.index", false);
        defaults.put("cds.contributions.features.quickFixes", false);
        defaults.put("cds.contributions.features.semanticHighlighting", false);
        defaults.put("cds.contributions.registry", "https://registry.npmjs.org");
        defaults.put("cds.diagnosticsSeverity", "Warning");
        defaults.put("cds.outline.elements.associationComposition", true);
        defaults.put("cds.outline.semantical", true);
        defaults.put("cds.quickfix.importArtifact", false);
        defaults.put("cds.refactoring.files.delete.enabled", false);
        defaults.put("cds.refactoring.files.rename.enabled", false);
        defaults.put("cds.semanticHighlighting.enabled", false);
        defaults.put("cds.semanticHighlighting.odata.enabled", false);
        defaults.put("cds.typeGenerator.command", "node \"${typerBinary}\" \"${targetFile}\" --outputDirectory \"${outputDirectory}\"");
        defaults.put("cds.typeGenerator.enabled", true);
        defaults.put("cds.typeGenerator.localInstallationOnly", true);
        defaults.put("cds.typeGenerator.outputPath", "@cds-models");
        defaults.put("cds.whereused.showGenericAnnotations", false);
        defaults.put("cds.whereused.showStringConstants", false);
        defaults.put("cds.workspace.persistency.enabled", false);
        defaults.put("cds.workspace.persistency.garbageCollectOrphanedIndexesAfterNSaves", 0);
        defaults.put("cds.workspace.persistency.garbageCollectOrphanedIndexesAfterStartup", false);
        defaults.put("cds.workspace.persistency.indexAllAfterStartup", false);
        defaults.put("cds.workspace.persistency.persistAfterCompile", false);
        defaults.put("cds.workspace.persistency.persistAfterSave", false);
        defaults.put("cds.workspace.persistency.reindexAfterCompileIfRestored", false);
        defaults.put("cds.workspace.persistency.restoreAfterStartup", false);
        defaults.put("cds.workspace.persistency.restoreBeforeCompile", false);
        defaults.put("cds.workspace.scanCsn", "ByFileExtension");
        defaults.put("cds.workspace.scanDependentModules", false);
        defaults.put("cds.workspaceSymbols.caseInsensitive", false);
        defaults.put("cds.workspaceSymbols.lazy", false);
        defaults.put("cds.workspaceValidationMode", "ActiveEditorOnly");
        DEFAULTS = Collections.unmodifiableMap(defaults);
    }

    public static Map<String, Object> getDefaults() {
        return DEFAULTS;
    }
}
