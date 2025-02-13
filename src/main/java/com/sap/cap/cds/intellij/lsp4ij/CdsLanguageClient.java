package com.sap.cap.cds.intellij.lsp4ij;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.intellij.openapi.project.Project;
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl;
import com.redhat.devtools.lsp4ij.server.definition.launching.UserDefinedLanguageClient;
import kotlinx.serialization.json.Json;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CdsLanguageClient extends LanguageClientImpl /*TODO: try UserDefinedLanguageClient*/ {
    public CdsLanguageClient(Project project) {
        super(project);
    }

    @Override
    protected Object createSettings() {
        return this.getSettings();
    }

    @Override
    public void telemetryEvent(Object object) {
        // TODO: can we use this to send telemetry data to out open telemetry cloud?
        super.telemetryEvent(object);
    }


    public JsonObject getSettings() {

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

        var compiler = addChild.run(cds, "compiler");
        compiler.addProperty("markMissingI18nDefault", true);                       // cds.compiler.markMissingI18nDefault (false) -> true

        var completion = addChild.run(cds, "completion");
        var docFiles = new JsonArray();
        docFiles.add("README.md");
        completion.add("docFiles", docFiles); // cds.completion.docFiles -> ['README.md']
        completion.addProperty("formatSnippets", true);                             // cds.completion.formatSnippets (false) -> true
        var symbols = addChild.run(completion, "workspaceSymbols");
        symbols.addProperty("minPrefixLength", 2);                                  // cds.completion.workspaceSymbols.minPrefixLength (-1) -> 2

//        var contributions = addChild.run(cds, "contributions");
//        contributions.put("enablement", Collections.singletonMap("odata", true)); // cds.contributions.enablement.odata (true) -> TODO: UI option

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
//    Map<String,Object> run(Map<String,Object> parent, String child);
    JsonObject run(JsonObject parent, String child);
}