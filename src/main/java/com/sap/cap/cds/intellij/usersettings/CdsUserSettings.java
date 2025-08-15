package com.sap.cap.cds.intellij.usersettings;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

import java.util.HashMap;
import java.util.Map;

@Service(Service.Level.PROJECT)
public class CdsUserSettings {

    private static Map<String, Object> defaultSettings = getDefaults();
    private Map<String, Object> settings;

    public CdsUserSettings(Project project) {
    }

    public static CdsUserSettings getInstance(Project project) {
        return project.getService(CdsUserSettings.class);
    }

    public static String getLabel(String settingKey) {
        switch (settingKey) {
            case "cds.codeLensStatistics.enabled": return "Code Lens Statistics";
            case "cds.compiler.markMissingI18nDefault": return "Mark Missing I18n Default";
            case "cds.compiler.showInternalErrors": return "Show Internal Errors";
            case "cds.completion.annotations.omitRedundantTypesInSnippets": return "Omit Redundant Types in Snippets";
            case "cds.completion.docFiles": return "Documentation Files";
            case "cds.completion.formatSnippets": return "Format Snippets";
            case "cds.completion.showDocumentation": return "Show Documentation";
            case "cds.completion.workspaceSymbols.maxProposals": return "Max Proposals";
            case "cds.completion.workspaceSymbols.minPrefixLength": return "Min Prefix Length";
            case "cds.contributions.enablement.additionalAnalyticalAnnotations": return "Additional Analytical Annotations";
            case "cds.contributions.enablement.odata": return "OData";
            case "cds.contributions.registry": return "Registry";
            case "cds.diagnosticsSeverity": return "Severity Level";
            case "cds.outline.elements.associationComposition": return "Show Association/Composition";
            case "cds.outline.semantical": return "Semantical";
            case "cds.quickfix.importArtifact": return "Import Artifact";
            case "cds.refactoring.files.delete.enabled": return "Update Usings on Delete";
            case "cds.refactoring.files.rename.enabled": return "Update Usings on Rename";
            case "cds.semanticHighlighting.enabled": return "Semantic Highlighting";
            case "cds.semanticHighlighting.odata.enabled": return "OData Semantic Highlighting";
            case "cds.typeGenerator.command": return "Command";
            case "cds.typeGenerator.enabled": return "Enabled";
            case "cds.typeGenerator.localInstallationOnly": return "Local Installation Only";
            case "cds.typeGenerator.outputPath": return "Output Path";
            case "cds.whereused.showGenericAnnotations": return "Show Generic Annotations";
            case "cds.whereused.showStringConstants": return "Show String Literals";
            case "cds.workspace.debounceFastChanges": return "Debounce Fast Changes";
            case "cds.workspace.fastDiagnosticsMode": return "Fast Diagnostics Mode";
            case "cds.workspace.scanCsn": return "Scan CSN Files";
            case "cds.workspace.scanDependentModules": return "Scan Dependent Modules";
            case "cds.workspaceSymbols.caseInsensitive": return "Case-Insensitive Symbols";
            case "cds.workspaceSymbols.lazy": return "Lazy Load Symbols";
            case "cds.workspaceValidationMode": return "Validation Mode";
            default: return null;
        }
    }

    public static boolean hasEnumValues(String settingKey) {
        return getEnumValues(settingKey) != null;
    }

    public static String[] getEnumValues(String settingKey) {
        switch (settingKey) {
            case "cds.diagnosticsSeverity": return new String[]{"Error", "Warning", "Info", "Hint"};
            case "cds.workspace.fastDiagnosticsMode": return new String[]{"Clear", "Partial", "Full"};
            case "cds.workspace.scanCsn": return new String[]{"BY_FILE_EXTENSION", "ALWAYS", "NEVER"};
            case "cds.workspaceValidationMode": return new String[]{"OpenEditorsOnly", "ActiveEditorOnly", "All"};
            default: return null;
        }
    }

    private static Map<String, Object> getDefaults() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("cds.codeLensStatistics.enabled", false);
        defaults.put("cds.compiler.markMissingI18nDefault", false);
        defaults.put("cds.compiler.showInternalErrors", false);
        defaults.put("cds.completion.annotations.omitRedundantTypesInSnippets", false);
        defaults.put("cds.completion.docFiles", "README.md");
        defaults.put("cds.completion.formatSnippets", false);
        defaults.put("cds.completion.showDocumentation", true);
        defaults.put("cds.completion.workspaceSymbols.maxProposals", -1);
        defaults.put("cds.completion.workspaceSymbols.minPrefixLength", -1);
        defaults.put("cds.contributions.enablement.additionalAnalyticalAnnotations", false);
        defaults.put("cds.contributions.enablement.odata", true);
        defaults.put("cds.contributions.registry", "https://registry.npmjs.org/");
        defaults.put("cds.diagnosticsSeverity", "Warning");
        defaults.put("cds.outline.elements.associationComposition", true);
        defaults.put("cds.outline.semantical", false);
        defaults.put("cds.quickfix.importArtifact", false);
        defaults.put("cds.refactoring.files.delete.enabled", true);
        defaults.put("cds.refactoring.files.rename.enabled", true);
        defaults.put("cds.semanticHighlighting.enabled", false);
        defaults.put("cds.semanticHighlighting.odata.enabled", false);
        defaults.put("cds.typeGenerator.command", "${typerBinary} \"${targetFile}\" --outputDirectory \"${outputDirectory}\"");
        defaults.put("cds.typeGenerator.enabled", false);
        defaults.put("cds.typeGenerator.localInstallationOnly", true);
        defaults.put("cds.typeGenerator.outputPath", "./@cds-models");
        defaults.put("cds.whereused.showGenericAnnotations", false);
        defaults.put("cds.whereused.showStringConstants", false);
        defaults.put("cds.workspace.debounceFastChanges", true);
        defaults.put("cds.workspace.fastDiagnosticsMode", "Clear");
        defaults.put("cds.workspace.scanCsn", "BY_FILE_EXTENSION");
        defaults.put("cds.workspace.scanDependentModules", false);
        defaults.put("cds.workspaceSymbols.caseInsensitive", false);
        defaults.put("cds.workspaceSymbols.lazy", false);
        defaults.put("cds.workspaceValidationMode", "OpenEditorsOnly");
        return defaults;
    }

    public Map<String, Object> getSettings() {
        if (settings == null) {
            settings = getDefaultSettings();
        }
        return settings;
    }

    public Map<String, Object> getDefaultSettings() {
        return defaultSettings;
    }
}
