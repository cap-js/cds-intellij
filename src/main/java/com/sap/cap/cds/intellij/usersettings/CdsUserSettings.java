package com.sap.cap.cds.intellij.usersettings;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

import java.util.Collections;
import java.util.LinkedHashMap;
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
            case "cds.compiler.markMissingI18nDefault" -> "Mark Missing I18n Default";
            case "cds.compiler.showInternalErrors" -> "Show Internal Errors";
            case "cds.completion.docFiles" -> "Documentation Files";
            case "cds.completion.formatSnippets" -> "Format Snippets";
            case "cds.completion.showDocumentation" -> "Show Documentation";
            case "cds.completion.annotations.omitRedundantTypesInSnippets" -> "Omit Redundant Types in Snippets";
            case "cds.completion.workspaceSymbols.maxProposals" -> "Max Proposals";
            case "cds.completion.workspaceSymbols.minPrefixLength" -> "Min Prefix Length";
            case "cds.completion.workspaceSymbols.useShortname" -> "Use Short Name";
            case "cds.contributions.registry" -> "Registry";
            case "cds.contributions.enablement.additionalAnalyticalAnnotations" -> "Additional Analytical Annotations";
            case "cds.contributions.features.completion" -> "Completion";
            case "cds.contributions.features.diagnostics" -> "Diagnostics";
            case "cds.contributions.features.hover" -> "Hover";
            case "cds.contributions.features.index" -> "Index";
            case "cds.contributions.enablement.odata" -> "OData";
            case "cds.contributions.features.quickFixes" -> "Quick Fixes";
            case "cds.contributions.features.semanticHighlighting" -> "Semantic Highlighting";
            case "cds.diagnosticsSeverity" -> "Severity Level";
            case "cds.semanticHighlighting.enabled" -> "Semantic Highlighting";
            case "cds.quickfix.importArtifact" -> "Import Artifact";
            case "cds.outline.semantical" -> "Semantical";
            case "cds.outline.elements.associationComposition" -> "Show Association/Composition";
            case "cds.refactoring.files.delete.enabled" -> "Update Usings on Delete";
            case "cds.refactoring.files.rename.enabled" -> "Update Usings on Rename";
            case "cds.whereused.showGenericAnnotations" -> "Show Generic Annotations";
            case "cds.whereused.showStringConstants" -> "Show String Literals";
            case "cds.typeGenerator.enabled" -> "Enabled";
            case "cds.typeGenerator.command" -> "Command";
            case "cds.typeGenerator.outputPath" -> "Output Path";
            case "cds.workspace.persistency.enabled" -> "Persistency Enabled";
            case "cds.workspace.persistency.restoreAfterStartup" -> "Restore After Startup";
            case "cds.workspace.persistency.restoreBeforeCompile" -> "Restore Before Compile";
            case "cds.workspace.persistency.persistAfterCompile" -> "Persist After Compile";
            case "cds.workspace.persistency.persistAfterSave" -> "Persist After Save";
            case "cds.workspace.persistency.indexAllAfterStartup" -> "Index All After Startup";
            case "cds.workspace.persistency.reindexAfterCompileIfRestored" -> "Reindex After Compile If Restored";
            case "cds.workspace.persistency.garbageCollectOrphanedIndexesAfterStartup" -> "Garbage Collect After Startup";
            case "cds.workspace.persistency.garbageCollectOrphanedIndexesAfterNSaves" -> "Garbage Collect After N Saves";
            case "cds.workspace.scanCsn" -> "Scan CSN Files";
            case "cds.workspace.scanDependentModules" -> "Scan Dependent Modules";
            case "cds.workspaceValidationMode" -> "Validation Mode";
            case "cds.workspaceSymbols.caseInsensitive" -> "Case-Insensitive Symbols";
            case "cds.workspaceSymbols.lazy" -> "Lazy Load Symbols";
            case "sapbas.telemetryEnabled" -> "Enable Telemetry";
            default -> null;
        };
    }

    // Note: method body is generated
    public static String getDescription(String settingKey) {
        return switch (settingKey) {
            case "cds.compiler.markMissingI18nDefault" -> "Show warning problem markers for unresolved <code>i18n</code> references.<br><br><b>NOTE:</b> For huge models it may show performance degradations";
            case "cds.compiler.showInternalErrors" -> "Print internal compiler errors to the console";
            case "cds.completion.docFiles" -> "Potential names of files to show as documentation. This is an ordered list. The first filename that exists is used.";
            case "cds.completion.formatSnippets" -> "Format snippets after applying in code completion";
            case "cds.completion.showDocumentation" -> "Show documentation in code completion";
            case "cds.completion.annotations.omitRedundantTypesInSnippets" -> "Omit redundant record types in suggested snippets";
            case "cds.completion.workspaceSymbols.maxProposals" -> "Maximum number of workspace symbols to suggest. Default is <code>-1</code> (all)";
            case "cds.completion.workspaceSymbols.minPrefixLength" -> "Number of characters required to suggest (global) workspace symbols. Default is <code>-1</code> (switched off - the feature may delay completion)";
            case "cds.completion.workspaceSymbols.useShortname" -> "When using workspace symbols proposals, only match the short name (last name segment) instead of the fully qualified name";
            case "cds.contributions.registry" -> "NPM registry to be used for installation/update of contributions (e.g. OData annotation support)";
            case "cds.contributions.enablement.additionalAnalyticalAnnotations" -> "Provide support for additional annotations used CAP Embedded Analytics<br><br>See capire (https://cap.cloud.sap/docs) for details";
            case "cds.contributions.features.completion" -> "Enable code completion for annotations";
            case "cds.contributions.features.diagnostics" -> "Enable diagnostics for annotations";
            case "cds.contributions.features.hover" -> "Enable hover information for annotations";
            case "cds.contributions.features.index" -> "Enable <i>Goto Definition</i> and <i>Find References</i> for annotations";
            case "cds.contributions.enablement.odata" -> "Provide extended annotation support for OData";
            case "cds.contributions.features.quickFixes" -> "Enable quick fixes for annotations";
            case "cds.contributions.features.semanticHighlighting" -> "Enable semantic highlighting for annotations (additionally requires <code>cds.semanticHighlighting.enabled</code> to be enabled)";
            case "cds.diagnosticsSeverity" -> "Minimum severity of compiler messages to show in <i>Problems</i> view";
            case "cds.semanticHighlighting.enabled" -> "Semantic highlighting of certain identifiers";
            case "cds.quickfix.importArtifact" -> "Provide a <i>quick fix</i> for artifacts not yet imported (default disabled - the feature is CPU intensive)";
            case "cds.outline.semantical" -> "Show a semantical outline structure as opposed to the (default) flat list";
            case "cds.outline.elements.associationComposition" -> "Use specific icons for <i>Association</i> and <i>Composition</i> elements in <i>Outline</i>";
            case "cds.refactoring.files.delete.enabled" -> "Adapt <code>using</code> statements in other files when deleting CDS files";
            case "cds.refactoring.files.rename.enabled" -> "Adapt <code>using</code> statements in other files when renaming CDS files";
            case "cds.whereused.showGenericAnnotations" -> "Find usages of same annotation names via <i>References</i> command and explicit annotation definitions via <i>Definition</i> command";
            case "cds.whereused.showStringConstants" -> "Find same string constants via <i>References</i> command";
            case "cds.typeGenerator.enabled" -> "Generate type definitions.<br>Requires <code>@cap-js/cds-typer</code> to be installed";
            case "cds.typeGenerator.command" -> "The command that is executed when generating model types. You can use the following variables in your command template:<br><br>- <code>${typerBinary}</code>: the binary<br>- <code>${targetFile}</code>: the file that is being typed<br>- <code>${outputDirectory}</code>: the directory into which the type information is generated";
            case "cds.typeGenerator.outputPath" -> "Directory that serves as root for the generated type definitions. Relative to the project's root";
            case "cds.workspace.persistency.enabled" -> "(Experimental) Enable persistency of where-used indexes for faster access of references and workspace symbols";
            case "cds.workspace.persistency.restoreAfterStartup" -> "At startup restore all persisted where-used indexes";
            case "cds.workspace.persistency.restoreBeforeCompile" -> "When compiling a model reuse persisted where-used indexes of dependent models";
            case "cds.workspace.persistency.persistAfterCompile" -> "Persist where-used indexes after a compile e.g. when changing a file";
            case "cds.workspace.persistency.persistAfterSave" -> "Persist where-used index of a CDS model file after save";
            case "cds.workspace.persistency.indexAllAfterStartup" -> "At startup index missing/outdated where-used indexes";
            case "cds.workspace.persistency.reindexAfterCompileIfRestored" -> "Ignore a persisted where-used index and reindex at compile even if the source content has not changed";
            case "cds.workspace.persistency.garbageCollectOrphanedIndexesAfterStartup" -> "At startup remove outdated where-used index files";
            case "cds.workspace.persistency.garbageCollectOrphanedIndexesAfterNSaves" -> "Remove outdated where-used index files when a certain number of saves are done";
            case "cds.workspace.scanCsn" -> "How CSN files are detected:<br>- <code>ByFileExtension</code> (default): CSN files (.csn or .csn.json) will be included in validation and workspace symbols<br>- <code>InspectJson</code>: additionally looks into .json files if they are valid CSN. This will take considerably more time at scanning<br>- <code>Off</code>: will not scan for CSN files at all";
            case "cds.workspace.scanDependentModules" -> "Scan first level dependencies in node_modules. This may increase start-up time. Enable if you want code completions for global identifiers and import paths, or want to see definitions of dependencies in workspace symbols";
            case "cds.workspaceValidationMode" -> "Specify which CDS files are validated:<br>- <code>ActiveEditorOnly</code> (default): only the active editor is validated<br>- <code>OpenEditorsOnly</code> additionally keeps other open editors' validation up-to-date";
            case "cds.workspaceSymbols.caseInsensitive" -> "<i>Workspace Symbols</i> will show symbols containing the given query, independent of character casings";
            case "cds.workspaceSymbols.lazy" -> "<i>Workspace Symbols</i> will show symbols that have the queried characters in the right order, but not necessarily consecutive";
            case "sapbas.telemetryEnabled" -> "Enable collecting usage analytics data. If enabled, non-personally identifiable information is used to help understand the product usage and improve the tool.";
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
            case "cds.diagnosticsSeverity" -> new String[]{"Error", "Warning", "Info", "Hint"};
            case "cds.workspace.scanCsn" -> new String[]{"Off", "ByFileExtension", "InspectJson"};
            case "cds.workspaceValidationMode" -> new String[]{"ActiveEditorOnly", "OpenEditorsOnly"};
            default -> null;
        };
    }

    // Note: method body is generated
    public static String getGroup(String settingKey) {
        return switch (settingKey) {
            case "cds.completion.annotations.omitRedundantTypesInSnippets" -> "Annotations";
            case "cds.completion.workspaceSymbols.maxProposals" -> "Workspace Symbols";
            case "cds.completion.workspaceSymbols.minPrefixLength" -> "Workspace Symbols";
            case "cds.completion.workspaceSymbols.useShortname" -> "Workspace Symbols";
            case "cds.contributions.enablement.additionalAnalyticalAnnotations" -> "Features";
            case "cds.contributions.features.completion" -> "Features";
            case "cds.contributions.features.diagnostics" -> "Features";
            case "cds.contributions.features.hover" -> "Features";
            case "cds.contributions.features.index" -> "Features";
            case "cds.contributions.features.quickFixes" -> "Features";
            case "cds.contributions.features.semanticHighlighting" -> "Features";
            case "cds.quickfix.importArtifact" -> "Quick Fix";
            case "cds.workspace.persistency.enabled" -> "Persistency";
            case "cds.workspace.persistency.restoreAfterStartup" -> "Persistency";
            case "cds.workspace.persistency.restoreBeforeCompile" -> "Persistency";
            case "cds.workspace.persistency.persistAfterCompile" -> "Persistency";
            case "cds.workspace.persistency.persistAfterSave" -> "Persistency";
            case "cds.workspace.persistency.indexAllAfterStartup" -> "Persistency";
            case "cds.workspace.persistency.reindexAfterCompileIfRestored" -> "Persistency";
            case "cds.workspace.persistency.garbageCollectOrphanedIndexesAfterStartup" -> "Persistency";
            case "cds.workspace.persistency.garbageCollectOrphanedIndexesAfterNSaves" -> "Persistency";
            case "cds.workspace.scanCsn" -> "Scanning";
            case "cds.workspace.scanDependentModules" -> "Scanning";
            case "cds.workspaceValidationMode" -> "Validation";
            case "cds.workspaceSymbols.caseInsensitive" -> "Workspace Symbols";
            case "cds.workspaceSymbols.lazy" -> "Workspace Symbols";
            default -> null;
        };
    }

    // Note: method body is generated
    public static String getCategory(String settingKey) {
        return switch (settingKey) {
            case "cds.compiler.markMissingI18nDefault" -> "Diagnostics";
            case "cds.compiler.showInternalErrors" -> "Editor";
            case "cds.completion.docFiles" -> "Completion";
            case "cds.completion.formatSnippets" -> "Completion";
            case "cds.completion.showDocumentation" -> "Completion";
            case "cds.completion.annotations.omitRedundantTypesInSnippets" -> "Completion";
            case "cds.completion.workspaceSymbols.maxProposals" -> "Completion";
            case "cds.completion.workspaceSymbols.minPrefixLength" -> "Completion";
            case "cds.completion.workspaceSymbols.useShortname" -> "Completion";
            case "cds.contributions.registry" -> "Contributions";
            case "cds.contributions.enablement.additionalAnalyticalAnnotations" -> "Contributions";
            case "cds.contributions.features.completion" -> "Contributions";
            case "cds.contributions.features.diagnostics" -> "Contributions";
            case "cds.contributions.features.hover" -> "Contributions";
            case "cds.contributions.features.index" -> "Contributions";
            case "cds.contributions.enablement.odata" -> "Contributions";
            case "cds.contributions.features.quickFixes" -> "Contributions";
            case "cds.contributions.features.semanticHighlighting" -> "Contributions";
            case "cds.diagnosticsSeverity" -> "Diagnostics";
            case "cds.semanticHighlighting.enabled" -> "Editor";
            case "cds.quickfix.importArtifact" -> "Diagnostics";
            case "cds.outline.semantical" -> "Outline";
            case "cds.outline.elements.associationComposition" -> "Outline";
            case "cds.refactoring.files.delete.enabled" -> "Editor";
            case "cds.refactoring.files.rename.enabled" -> "Editor";
            case "cds.whereused.showGenericAnnotations" -> "Search";
            case "cds.whereused.showStringConstants" -> "Search";
            case "cds.typeGenerator.enabled" -> "Type Generator";
            case "cds.typeGenerator.command" -> "Type Generator";
            case "cds.typeGenerator.outputPath" -> "Type Generator";
            case "cds.workspace.persistency.enabled" -> "Search";
            case "cds.workspace.persistency.restoreAfterStartup" -> "Search";
            case "cds.workspace.persistency.restoreBeforeCompile" -> "Search";
            case "cds.workspace.persistency.persistAfterCompile" -> "Search";
            case "cds.workspace.persistency.persistAfterSave" -> "Search";
            case "cds.workspace.persistency.indexAllAfterStartup" -> "Search";
            case "cds.workspace.persistency.reindexAfterCompileIfRestored" -> "Search";
            case "cds.workspace.persistency.garbageCollectOrphanedIndexesAfterStartup" -> "Search";
            case "cds.workspace.persistency.garbageCollectOrphanedIndexesAfterNSaves" -> "Search";
            case "cds.workspace.scanCsn" -> "Diagnostics";
            case "cds.workspace.scanDependentModules" -> "Diagnostics";
            case "cds.workspaceValidationMode" -> "Diagnostics";
            case "cds.workspaceSymbols.caseInsensitive" -> "Outline";
            case "cds.workspaceSymbols.lazy" -> "Outline";
            case "sapbas.telemetryEnabled" -> "Telemetry";
            default -> null;
        };
    }

    public Map<String, Object> getSettings() {
        if (settings == null) {
            settings = new LinkedHashMap<>(getDefaults());
        }
        return settings;
    }

    private static final Map<String, Object> DEFAULTS;

    // Note: static initializer is generated
    static {
        Map<String, Object> defaults = new LinkedHashMap<>();
        defaults.put("cds.compiler.markMissingI18nDefault", false);
        defaults.put("cds.compiler.showInternalErrors", false);
        defaults.put("cds.completion.docFiles", "README.md");
        defaults.put("cds.completion.formatSnippets", false);
        defaults.put("cds.completion.showDocumentation", true);
        defaults.put("cds.completion.annotations.omitRedundantTypesInSnippets", false);
        defaults.put("cds.completion.workspaceSymbols.maxProposals", -1);
        defaults.put("cds.completion.workspaceSymbols.minPrefixLength", -1);
        defaults.put("cds.completion.workspaceSymbols.useShortname", false);
        defaults.put("cds.contributions.registry", "https://registry.npmjs.org/");
        defaults.put("cds.contributions.enablement.additionalAnalyticalAnnotations", false);
        defaults.put("cds.contributions.features.completion", true);
        defaults.put("cds.contributions.features.diagnostics", false);
        defaults.put("cds.contributions.features.hover", true);
        defaults.put("cds.contributions.features.index", false);
        defaults.put("cds.contributions.enablement.odata", true);
        defaults.put("cds.contributions.features.quickFixes", false);
        defaults.put("cds.contributions.features.semanticHighlighting", false);
        defaults.put("cds.diagnosticsSeverity", "Warning");
        defaults.put("cds.semanticHighlighting.enabled", false);
        defaults.put("cds.quickfix.importArtifact", false);
        defaults.put("cds.outline.semantical", false);
        defaults.put("cds.outline.elements.associationComposition", true);
        defaults.put("cds.refactoring.files.delete.enabled", true);
        defaults.put("cds.refactoring.files.rename.enabled", true);
        defaults.put("cds.whereused.showGenericAnnotations", false);
        defaults.put("cds.whereused.showStringConstants", false);
        defaults.put("cds.typeGenerator.enabled", false);
        defaults.put("cds.typeGenerator.command", "${typerBinary} \"${targetFile}\" --outputDirectory \"${outputDirectory}\"");
        defaults.put("cds.typeGenerator.outputPath", "./@cds-models");
        defaults.put("cds.workspace.persistency.enabled", false);
        defaults.put("cds.workspace.persistency.restoreAfterStartup", false);
        defaults.put("cds.workspace.persistency.restoreBeforeCompile", true);
        defaults.put("cds.workspace.persistency.persistAfterCompile", true);
        defaults.put("cds.workspace.persistency.persistAfterSave", true);
        defaults.put("cds.workspace.persistency.indexAllAfterStartup", true);
        defaults.put("cds.workspace.persistency.reindexAfterCompileIfRestored", false);
        defaults.put("cds.workspace.persistency.garbageCollectOrphanedIndexesAfterStartup", true);
        defaults.put("cds.workspace.persistency.garbageCollectOrphanedIndexesAfterNSaves", 20);
        defaults.put("cds.workspace.scanCsn", "ByFileExtension");
        defaults.put("cds.workspace.scanDependentModules", false);
        defaults.put("cds.workspaceValidationMode", "OpenEditorsOnly");
        defaults.put("cds.workspaceSymbols.caseInsensitive", false);
        defaults.put("cds.workspaceSymbols.lazy", false);
        defaults.put("sapbas.telemetryEnabled", true);
        DEFAULTS = Collections.unmodifiableMap(defaults);
    }

    public static Map<String, Object> getDefaults() {
        return DEFAULTS;
    }
}
