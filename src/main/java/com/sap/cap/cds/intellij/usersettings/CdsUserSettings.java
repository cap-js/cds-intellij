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
    public static String getDescription(String settingKey) {
        return switch (settingKey) {
            case "cds.contributions.enablement.odata" -> "Provide extended annotation support for OData";
            case "cds.contributions.features.completion" -> "Enable code completion for annotations";
            case "cds.contributions.features.diagnostics" -> "Enable diagnostics for annotations";
            case "cds.contributions.features.hover" -> "Enable hover information for annotations";
            case "cds.contributions.features.index" -> "Enable <i>Goto Definition</i> and <i>Find References</i> for annotations";
            case "cds.contributions.features.quickFixes" -> "Enable quick fixes for annotations";
            case "cds.contributions.features.semanticHighlighting" -> "Enable semantic highlighting for annotations (additionally requires <code>cds.semanticHighlighting.enabled</code> to be enabled)";
            case "cds.contributions.enablement.additionalAnalyticalAnnotations" -> "Provide support for additional annotations used CAP Embedded Analytics<br>See capire (https://cap.cloud.sap/docs) for details";
            case "cds.contributions.registry" -> "NPM registry to be used for installation/update of contributions (e.g. OData annotation support)";
            case "cds.completion.showDocumentation" -> "Show documentation in code completion";
            case "cds.completion.docFiles" -> "Potential names of files to show as documentation. This is an ordered list. The first filename that exists is used.";
            case "cds.completion.formatSnippets" -> "Format snippets after applying in code completion";
            case "cds.completion.annotations.omitRedundantTypesInSnippets" -> "Omit redundant record types in suggested snippets";
            case "cds.completion.workspaceSymbols.minPrefixLength" -> "Number of characters required to suggest (global) workspace symbols. Default is <code>-1</code> (switched off - the feature may delay completion)";
            case "cds.completion.workspaceSymbols.maxProposals" -> "Maximum number of workspace symbols to suggest. Default is <code>-1</code> (all)";
            case "cds.completion.workspaceSymbols.useShortname" -> "When using workspace symbols proposals, only match the short name (last name segment) instead of the fully qualified name";
            case "cds.whereused.showGenericAnnotations" -> "Find usages of same annotation names via <i>References</i> command and explicit annotation definitions via <i>Definition</i> command";
            case "cds.whereused.showStringConstants" -> "Find same string constants via <i>References</i> command";
            case "cds.workspace.persistency.enabled" -> "(Beta) Enable persistency of where-used indexes for faster access of references and workspace symbols";
            case "cds.workspace.persistency.persistAfterSave" -> "Persist where-used index of a CDS model file after save";
            case "cds.workspace.persistency.persistAfterCompile" -> "Persist where-used indexes after a compile e.g. when changing a file";
            case "cds.workspace.persistency.restoreBeforeCompile" -> "When compiling a model reuse persisted where-used indexes of dependent models";
            case "cds.workspace.persistency.restoreAfterStartup" -> "At startup restore all persisted where-used indexes";
            case "cds.workspace.persistency.indexAllAfterStartup" -> "At startup index missing/outdated where-used indexes";
            case "cds.workspace.persistency.garbageCollectOrphanedIndexesAfterStartup" -> "At startup remove outdated where-used index files";
            case "cds.workspace.persistency.garbageCollectOrphanedIndexesAfterNSaves" -> "Remove outdated where-used index files when a certain number of saves are done";
            case "cds.workspace.persistency.reindexAfterCompileIfRestored" -> "Ignore a persisted where-used index and reindex at compile even if the source content has not changed";
            case "cds.outline.semantical" -> "Show a semantical outline structure as opposed to the (default) flat list";
            case "cds.outline.elements.associationComposition" -> "Use specific icons for <i>Association</i> and <i>Composition</i> elements in <i>Outline</i>";
            case "cds.workspaceSymbols.lazy" -> "<i>Workspace Symbols</i> will show symbols that have the queried characters in the right order, but not necessarily consecutive";
            case "cds.workspaceSymbols.caseInsensitive" -> "<i>Workspace Symbols</i> will show symbols containing the given query, independent of character casings";
            case "cds.typeGenerator.enabled" -> "Generate type definitions.<br>Requires <code>@cap-js/cds-typer</code> to be installed";
            case "cds.typeGenerator.outputPath" -> "Directory that serves as root for the generated type definitions. Relative to the project's root";
            case "cds.typeGenerator.command" -> "The command that is executed when generating model types. You can use the following variables in your command template:<br>- <code>${typerBinary}</code>: the binary<br>- <code>${targetFile}</code>: the file that is being typed<br>- <code>${outputDirectory}</code>: the directory into which the type information is generated";
            case "sapbas.telemetryEnabled" -> "Enable collecting usage analytics data. If enabled, non-personally identifiable information is used to help understand the product usage and improve the tool.";
            case "cds.diagnosticsSeverity" -> "Minimum severity of compiler messages to show in <i>Problems</i> view";
            case "cds.compiler.markMissingI18nDefault" -> "Show warning problem markers for unresolved <code>i18n</code> references.<br><b>NOTE:</b> For huge models it may show performance degradations";
            case "cds.quickfix.importArtifact" -> "Provide a <i>quick fix</i> for artifacts not yet imported (default disabled - the feature is CPU intensive)";
            case "cds.workspace.scanDependentModules" -> "Scan first level dependencies in node_modules. This may increase start-up time. Enable if you want code completions for global identifiers and import paths, or want to see definitions of dependencies in workspace symbols";
            case "cds.workspaceValidationMode" -> "Specify which CDS files are validated:<br>- <code>ActiveEditorOnly</code> (default): only the active editor is validated<br>- <code>OpenEditorsOnly</code> additionally keeps other open editors' validation up-to-date";
            case "cds.workspace.scanCsn" -> "How CSN files are detected:<br>- <code>ByFileExtension</code> (default): CSN files (.csn or .csn.json) will be included in validation and workspace symbols<br>- <code>InspectJson</code>: additionally looks into .json files if they are valid CSN. This will take considerably more time at scanning<br>- <code>Off</code>: will not scan for CSN files at all";
            case "cds.semanticHighlighting.enabled" -> "Semantic highlighting of certain identifiers";
            case "cds.refactoring.files.delete.enabled" -> "Adapt <code>using</code> statements in other files when deleting CDS files";
            case "cds.refactoring.files.rename.enabled" -> "Adapt <code>using</code> statements in other files when renaming CDS files";
            case "cds.compiler.showInternalErrors" -> "Print internal compiler errors to the console";
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
            case "cds.workspaceValidationMode" -> new String[]{"ActiveEditorOnly", "OpenEditorsOnly"};
            case "cds.workspace.scanCsn" -> new String[]{"Off", "ByFileExtension", "InspectJson"};
            default -> null;
        };
    }

    // Note: method body is generated
    public static String getCategory(String settingKey) {
        return switch (settingKey) {
            case "cds.contributions.enablement.odata" -> "Annotation Support";
            case "cds.contributions.features.completion" -> "Annotation Support";
            case "cds.contributions.features.diagnostics" -> "Annotation Support";
            case "cds.contributions.features.hover" -> "Annotation Support";
            case "cds.contributions.features.index" -> "Annotation Support";
            case "cds.contributions.features.quickFixes" -> "Annotation Support";
            case "cds.contributions.features.semanticHighlighting" -> "Annotation Support";
            case "cds.contributions.enablement.additionalAnalyticalAnnotations" -> "Annotation Support";
            case "cds.contributions.registry" -> "Annotation Support";
            case "cds.completion.showDocumentation" -> "Code Completion";
            case "cds.completion.docFiles" -> "Code Completion";
            case "cds.completion.formatSnippets" -> "Code Completion";
            case "cds.completion.annotations.omitRedundantTypesInSnippets" -> "Code Completion";
            case "cds.completion.workspaceSymbols.minPrefixLength" -> "Code Completion";
            case "cds.completion.workspaceSymbols.maxProposals" -> "Code Completion";
            case "cds.completion.workspaceSymbols.useShortname" -> "Code Completion";
            case "cds.whereused.showGenericAnnotations" -> "Where-used";
            case "cds.whereused.showStringConstants" -> "Where-used";
            case "cds.workspace.persistency.enabled" -> "Where-used";
            case "cds.workspace.persistency.persistAfterSave" -> "Where-used";
            case "cds.workspace.persistency.persistAfterCompile" -> "Where-used";
            case "cds.workspace.persistency.restoreBeforeCompile" -> "Where-used";
            case "cds.workspace.persistency.restoreAfterStartup" -> "Where-used";
            case "cds.workspace.persistency.indexAllAfterStartup" -> "Where-used";
            case "cds.workspace.persistency.garbageCollectOrphanedIndexesAfterStartup" -> "Where-used";
            case "cds.workspace.persistency.garbageCollectOrphanedIndexesAfterNSaves" -> "Where-used";
            case "cds.workspace.persistency.reindexAfterCompileIfRestored" -> "Where-used";
            case "cds.outline.semantical" -> "Symbols";
            case "cds.outline.elements.associationComposition" -> "Symbols";
            case "cds.workspaceSymbols.lazy" -> "Symbols";
            case "cds.workspaceSymbols.caseInsensitive" -> "Symbols";
            case "cds.typeGenerator.enabled" -> "Type Generation";
            case "cds.typeGenerator.outputPath" -> "Type Generation";
            case "cds.typeGenerator.command" -> "Type Generation";
            case "sapbas.telemetryEnabled" -> "Telemetry";
            case "cds.diagnosticsSeverity" -> "Validation";
            case "cds.compiler.markMissingI18nDefault" -> "Validation";
            case "cds.quickfix.importArtifact" -> "Validation";
            case "cds.workspace.scanDependentModules" -> "Validation";
            case "cds.workspaceValidationMode" -> "Validation";
            case "cds.workspace.scanCsn" -> "Validation";
            case "cds.semanticHighlighting.enabled" -> "Misc";
            case "cds.refactoring.files.delete.enabled" -> "Misc";
            case "cds.refactoring.files.rename.enabled" -> "Misc";
            case "cds.compiler.showInternalErrors" -> "Misc";
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
        defaults.put("cds.contributions.enablement.odata", true);
        defaults.put("cds.contributions.features.completion", true);
        defaults.put("cds.contributions.features.diagnostics", false);
        defaults.put("cds.contributions.features.hover", true);
        defaults.put("cds.contributions.features.index", false);
        defaults.put("cds.contributions.features.quickFixes", false);
        defaults.put("cds.contributions.features.semanticHighlighting", false);
        defaults.put("cds.contributions.enablement.additionalAnalyticalAnnotations", false);
        defaults.put("cds.contributions.registry", "https://registry.npmjs.org/");
        defaults.put("cds.completion.showDocumentation", true);
        defaults.put("cds.completion.docFiles", "README.md");
        defaults.put("cds.completion.formatSnippets", false);
        defaults.put("cds.completion.annotations.omitRedundantTypesInSnippets", false);
        defaults.put("cds.completion.workspaceSymbols.minPrefixLength", -1);
        defaults.put("cds.completion.workspaceSymbols.maxProposals", -1);
        defaults.put("cds.completion.workspaceSymbols.useShortname", false);
        defaults.put("cds.whereused.showGenericAnnotations", false);
        defaults.put("cds.whereused.showStringConstants", false);
        defaults.put("cds.workspace.persistency.enabled", false);
        defaults.put("cds.workspace.persistency.persistAfterSave", true);
        defaults.put("cds.workspace.persistency.persistAfterCompile", true);
        defaults.put("cds.workspace.persistency.restoreBeforeCompile", true);
        defaults.put("cds.workspace.persistency.restoreAfterStartup", false);
        defaults.put("cds.workspace.persistency.indexAllAfterStartup", true);
        defaults.put("cds.workspace.persistency.garbageCollectOrphanedIndexesAfterStartup", true);
        defaults.put("cds.workspace.persistency.garbageCollectOrphanedIndexesAfterNSaves", 20);
        defaults.put("cds.workspace.persistency.reindexAfterCompileIfRestored", false);
        defaults.put("cds.outline.semantical", false);
        defaults.put("cds.outline.elements.associationComposition", true);
        defaults.put("cds.workspaceSymbols.lazy", false);
        defaults.put("cds.workspaceSymbols.caseInsensitive", false);
        defaults.put("cds.typeGenerator.enabled", false);
        defaults.put("cds.typeGenerator.outputPath", "./@cds-models");
        defaults.put("cds.typeGenerator.command", "${typerBinary} \"${targetFile}\" --outputDirectory \"${outputDirectory}\"");
        defaults.put("sapbas.telemetryEnabled", true);
        defaults.put("cds.diagnosticsSeverity", "Warning");
        defaults.put("cds.compiler.markMissingI18nDefault", false);
        defaults.put("cds.quickfix.importArtifact", false);
        defaults.put("cds.workspace.scanDependentModules", false);
        defaults.put("cds.workspaceValidationMode", "OpenEditorsOnly");
        defaults.put("cds.workspace.scanCsn", "ByFileExtension");
        defaults.put("cds.semanticHighlighting.enabled", false);
        defaults.put("cds.refactoring.files.delete.enabled", true);
        defaults.put("cds.refactoring.files.rename.enabled", true);
        defaults.put("cds.compiler.showInternalErrors", false);
        DEFAULTS = Collections.unmodifiableMap(defaults);
    }

    public static Map<String, Object> getDefaults() {
        return DEFAULTS;
    }
}
