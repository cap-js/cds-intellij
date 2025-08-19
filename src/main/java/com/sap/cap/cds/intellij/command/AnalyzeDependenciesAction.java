package com.sap.cap.cds.intellij.command;

import com.google.gson.JsonObject;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.redhat.devtools.lsp4ij.commands.CommandExecutor;
import com.redhat.devtools.lsp4ij.commands.LSPCommandContext;
import com.sap.cap.cds.intellij.lsp4ij.CdsLanguageServer;
import org.eclipse.lsp4j.Command;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class AnalyzeDependenciesAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Object data = anActionEvent.getDataContext();


//        		const startModelUri = this.getServerModelUri(uri);

//		const command: CommandName = 'analyze-dependencies';
//		const params: IAnalyzeDependenciesParams = {
//                command,
//                arguments: [{
//            startModelUri,
//                    outputFormat: 'svg',
//                    detailMode
//        }]
//		};
//		const svg = await this.getClient().sendRequest('workspace/executeCommand', params) as string;


        var project = anActionEvent.getProject();
        if (project == null) { return; }

        Command command = new Command("Analyze Dependencies", "analyze-dependencies");
        LSPCommandContext commandContext = new LSPCommandContext(command, project);
        commandContext.setPreferredLanguageServerId(CdsLanguageServer.ID);
        command.setArguments(Collections.singletonList(new JsonObject())); // TODO: Add IAnalyzeDependenciesParams
        CommandExecutor.executeCommand(commandContext)
                .response()
                .thenAccept(r -> {
                    // Do something with the workspace/executeCommand Object response
                });
    }
}
