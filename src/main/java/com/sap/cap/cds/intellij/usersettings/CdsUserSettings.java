package com.sap.cap.cds.intellij.usersettings;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

import java.util.HashMap;
import java.util.Map;

@Service(Service.Level.PROJECT)
public class CdsUserSettings {

    private final Project project;

    public CdsUserSettings(Project project) {
        this.project = project;
    }

    public static CdsUserSettings getInstance(Project project) {
        return project.getService(CdsUserSettings.class);
    }

    public Map<String, Object> getAllSettings() {
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
        defaults.put("cds.internal.inspectTokens", false);
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

    public static String getLabel(String settingKey) {
        switch (settingKey) {
                case "cds.codeLensStatistics.enabled": return "Enable Code Lens Statistics";
                case "cds.compiler.markMissingI18nDefault": return "Warn on Missing Translations";
                case "cds.compiler.showInternalErrors": return "Show Internal Compiler Errors";
                case "cds.completion.annotations.omitRedundantTypesInSnippets": return "Omit Redundant Types in Snippets";
                case "cds.completion.docFiles": return "Markdown Files for Documentation Provider";
                case "cds.completion.formatSnippets": return "Format Snippets";
                case "cds.completion.showDocumentation": return "Show Documentation in Completion";
                case "cds.completion.workspaceSymbols.maxProposals": return "Workspace Symbols Maximum Proposals";
                case "cds.completion.workspaceSymbols.minPrefixLength": return "Workspace Symbols Minimum Prefix Length";
                case "cds.contributions.enablement.additionalAnalyticalAnnotations": return "Enable Additional Analytical Annotations";
                case "cds.contributions.enablement.odata": return "Enable OData Contributions";
                case "cds.contributions.registry": return "Contributions Registry";
                case "cds.diagnosticsSeverity": return "Diagnostics Severity Level";
                case "cds.internal.inspectTokens": return "Inspect Tokens";
                case "cds.outline.elements.associationComposition": return "Show Association/Composition in Outline";
                case "cds.outline.semantical": return "Semantical Document Outline";
                case "cds.quickfix.importArtifact": return "Enable Import Artifact Quick Fix";
                case "cds.refactoring.files.delete.enabled": return "Update Usings on File Delete";
                case "cds.refactoring.files.rename.enabled": return "Update Usings on File Rename";
                case "cds.semanticHighlighting.enabled": return "Enable Semantic Highlighting";
                case "cds.semanticHighlighting.odata.enabled": return "Enable OData Semantic Highlighting";
                case "cds.typeGenerator.command": return "Type Generation Command";
                case "cds.typeGenerator.enabled": return "Enable Type Generator";
                case "cds.typeGenerator.localInstallationOnly": return "Use Local Installation Only";
                case "cds.typeGenerator.outputPath": return "Output Path for Type Generator";
                case "cds.whereused.showGenericAnnotations": return "Show Generic Annotations in 'Where-Used'";
                case "cds.whereused.showStringConstants": return "Show String Literals in 'Where-Used'";
                case "cds.workspace.debounceFastChanges": return "Debounce Fast Workspace Changes";
                case "cds.workspace.fastDiagnosticsMode": return "Fast Diagnostics Mode";
                case "cds.workspace.scanCsn": return "Scan CSN Files in Workspace";
                case "cds.workspace.scanDependentModules": return "Scan Dependent Modules";
                case "cds.workspaceSymbols.caseInsensitive": return "Case-Insensitive Workspace Symbols";
                case "cds.workspaceSymbols.lazy": return "Lazy Load Workspace Symbols";
                case "cds.workspaceValidationMode": return "Workspace Validation Mode";
                default: return null;
        }
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

    public static boolean hasEnumValues(String settingKey) {
        return getEnumValues(settingKey) != null;
    }
}
