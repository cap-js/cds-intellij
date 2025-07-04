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


        var settings = new JsonObject(); // new HashMap<String, Object>();

        var cds = addChild.run(settings, "cds");

        addChild.run(cds, "codeLensStatistics").addProperty("enabled", true); // cds.codeLensStatistics.enabled (false) -> true

        var compiler = addChild.run(cds, "compiler");
        compiler.addProperty("markMissingI18nDefault", true);                       // cds.compiler.markMissingI18nDefault (false) -> true

        var completion = addChild.run(cds, "completion");
        var docFiles = new JsonArray();
        docFiles.add("README.md");
        completion.add("docFiles", docFiles); // cds.completion.docFiles -> ['README.md']
        completion.addProperty("formatSnippets", true);                             // cds.completion.formatSnippets (false) -> true
        var symbols = addChild.run(completion, "workspaceSymbols");
        symbols.addProperty("minPrefixLength", 2);                                  // cds.completion.workspaceSymbols.minPrefixLength (-1) -> 2

        var contributions = addChild.run(cds, "contributions");
        var enablement = addChild.run(contributions, "enablement");
        enablement.addProperty("odata", false);                                    // cds.contributions.enablement.odata (true) -> false

        cds.addProperty("diagnosticsSeverity", "Info");                             // cds.diagnosticsSeverity (Warning) -> Info

        var outline = addChild.run(cds, "outline");
        outline.addProperty("semantical", true);                                    // cds.outline.semantical (false) -> true

        var quickfix = addChild.run(cds, "quickfix");
        quickfix.addProperty("importArtifact", true);                               // cds.quickfix.importArtifact (false) -> true

//        var refactoring = addChild.run(cds, "refactoring");
//        var files = addChild.run(refactoring, "files");
//        files.put("delete", Collections.singletonMap("enabled", true));     // cds.refactoring.files.delete.enabled (true)
//        files.put("rename", Collections.singletonMap("enabled", true));     // cds.refactoring.files.rename.enabled (true)

        var semanticHighlighting = addChild.run(cds, "semanticHighlighting");
        semanticHighlighting.addProperty("enabled", true);                          // cds.semanticHighlighting.enabled (false) -> true

//        var typeGenerator = addChild.run(cds, "typeGenerator");
//        typeGenerator.put("enabled", true);                                 // cds.typeGenerator.enabled (true) -> TODO: test

        var whereused = addChild.run(cds, "whereused");
        whereused.addProperty("showStringConstants", true);                         // cds.whereused.showStringConstants (false) -> true

        return settings;
    }

}


interface AddChild {
    JsonObject run(JsonObject parent, String child);
}