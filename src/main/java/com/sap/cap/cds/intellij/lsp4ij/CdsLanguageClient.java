package com.sap.cap.cds.intellij.lsp4ij;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.intellij.openapi.project.Project;
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl;
import org.eclipse.lsp4j.ApplyWorkspaceEditParams;
import org.eclipse.lsp4j.ApplyWorkspaceEditResponse;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class CdsLanguageClient extends LanguageClientImpl /*TODO: try UserDefinedLanguageClient or IndexAwareLanguageClient*/ {
    public CdsLanguageClient(Project project) {
        super(project);
    }

    @Override
    protected Object createSettings() {
        return this.getSettings();
    }

    @Override
    protected Object findSettings(@Nullable String section) {
        Object settings = super.findSettings(section);
        return settings;
    }

    // TODO: derive from UserDefinedLanguageClient and get this for free: sends workspace/didChangeConfiguration after server init
//    @Override
//    public void handleServerStatusChanged(ServerStatus serverStatus) {
//        if (serverStatus == ServerStatus.started) {
//            triggerChangeConfiguration();
//        }
//    }



    // TODO: register command/url handler for analyze dependency's "command:cds.analyzeDependencies", then...
    /*
CompletableFuture<List<Application>> applications =
  LanguageServerManager.getInstance(project)
    .getLanguageServer("myLanguageServerId")
    .thenApply(languageServerItem ->
                    languageServerItem != null ? languageServerItem.getServer() // here getServer is used because we are sure that server is initialized
                    : null)
    .thenCompose(ls -> {
      if (ls == null) {
          return CompletableFuture.completedFuture(Collections.emptyList());
      }
      MyCustomServerAPI myServer = (MyCustomServerAPI) ls;
      return myServer.getApplications();}  // custom request or other stuff...
    );
     */



    @Override
    public void telemetryEvent(Object object) {
        // TODO: can we use this to send telemetry data to out open telemetry cloud?
        super.telemetryEvent(object);
    }




    @Override
    public CompletableFuture<ApplyWorkspaceEditResponse> applyEdit(ApplyWorkspaceEditParams params) {
        // TODO: maintain translation should save the properties file Q: do we need to register for .properties files, too? We want a didChangeFile event to be send to the server
        return super.applyEdit(params);
    }


    public JsonObject getSettings() {
        return CdsLanguageClient.getInitializationOptions();
    }

    public static JsonObject getInitializationOptions() {
        // TODO: implement all settings in IntelliJ settings under Languages->CDS, read them here and return them


        // TODO cds.trace.level (off) -> verbose (set env and restart)
        // TODO implement ActiveEditorChanged request

        // Once we have a UI, we need to restart the LSP for some options to take effect

        AddChild addChild = (parent, child) -> {
            var map = new JsonObject();
            parent.add(child, map);
            return map;
        };


        var settings = new JsonObject();

        var cds = addChild.run(settings, "cds");

        // cds.codeLensStatistics.enabled (false) - this is an undocumented technical feature
        addChild.run(cds, "codeLensStatistics").addProperty("enabled", false);

        var cds_compiler = addChild.run(cds, "compiler");

        // cds.compiler.markMissingI18nDefault (false)
        cds_compiler.addProperty("markMissingI18nDefault", false);

        var cds_completion = addChild.run(cds, "completion");

        // cds.completion.docFiles -> ['README.md']
        {
            var docFiles = new JsonArray();
            docFiles.add("README.md");
            cds_completion.add("docFiles", docFiles);
        }

        // cds.completion.formatSnippets (false)
        cds_completion.addProperty("formatSnippets", false);

        // Do not suggest Workspace Symbols in code completion, this is currently a performance killer for larger projects
        var cds_completion_workspaceSymbols = addChild.run(cds_completion, "workspaceSymbols");
        cds_completion_workspaceSymbols.addProperty("minPrefixLength", -1); // disable
//      cds_completion_workspaceSymbols.addProperty("maxProposals", -1); // all

        // Disable Annotation Modeler for now (slow for large projects, needs LSP throttling)
        {
            var cds_contributions = addChild.run(cds, "contributions");
            var cds_contributions_enablement = addChild.run(cds_contributions, "enablement");
            cds_contributions_enablement.addProperty("odata", false);                                    // cds.contributions.enablement.odata (true) -> false
        }

        // cds.diagnosticsSeverity (Warning)
        cds.addProperty("diagnosticsSeverity", "Warning");

        // Use semantical (hierarchical) outline
        var cds_outline = addChild.run(cds, "outline");
        cds_outline.addProperty("semantical", true);                                    // cds.outline.semantical (false) -> true

        // Do not check unknown artifacts if present in workspace (requires Workspace Symbols => currently slow)
        var cds_quickfix = addChild.run(cds, "quickfix");
        cds_quickfix.addProperty("importArtifact", false);

        // Disable using path refactorings (currently slow in LSP)
        {
            var cds_refactoring = addChild.run(cds, "refactoring");
            var cds_refactoring_files = addChild.run(cds_refactoring, "files");

            var cds_refactoring_files_delete = addChild.run(cds_refactoring_files, "delete");
            cds_refactoring_files_delete.addProperty("enabled", false);

            var cds_refactoring_files_rename = addChild.run(cds_refactoring_files, "rename");
            cds_refactoring_files_rename.addProperty("enabled", false);
        }

        // Semantic Highlighting off
        var cds_semanticHighlighting = addChild.run(cds, "semanticHighlighting");
        cds_semanticHighlighting.addProperty("enabled", false);

        // Enable typer (true)
        var cds_typeGenerator = addChild.run(cds, "typeGenerator");
        cds_typeGenerator.addProperty("enabled", true);

        // Do not find same string literals (false)
        var cds_whereused = addChild.run(cds, "whereused");
        cds_whereused.addProperty("showStringConstants", false);

        return settings;
    }

}


interface AddChild {
    JsonObject run(JsonObject parent, String child);
}