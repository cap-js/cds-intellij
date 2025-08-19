package com.sap.cap.cds.intellij.lsp4ij;

import com.intellij.openapi.project.Project;
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl;
import com.sap.cap.cds.intellij.usersettings.CdsUserSettingsService;
import org.eclipse.lsp4j.ApplyWorkspaceEditParams;
import org.eclipse.lsp4j.ApplyWorkspaceEditResponse;

import java.util.concurrent.CompletableFuture;

public class CdsLanguageClient extends LanguageClientImpl /*TODO: try UserDefinedLanguageClient or IndexAwareLanguageClient*/ {
    private Project myProject;

    public CdsLanguageClient(Project project) {
        super(project);
        this.myProject = project;
    }

    @Override
    public void telemetryEvent(Object object) {
        // TODO: can we use this to send telemetry data to our open telemetry cloud?
        super.telemetryEvent(object);
    }

    @Override
    public CompletableFuture<ApplyWorkspaceEditResponse> applyEdit(ApplyWorkspaceEditParams params) {
        // TODO: maintain translation should save the properties file Q: do we need to register for .properties files, too? We want a didChangeFile event to be send to the server
        return super.applyEdit(params);
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
    protected Object createSettings() {
        return myProject.getService(CdsUserSettingsService.class)
                .getSettingsStructured();
    }

}