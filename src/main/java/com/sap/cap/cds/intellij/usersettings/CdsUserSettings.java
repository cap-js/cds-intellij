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
}
