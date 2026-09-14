package com.sap.cap.cds.intellij.util;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.execution.util.ExecUtil;

import java.util.List;
import java.util.Optional;

public class CliUtil {
    public static Optional<String> executeCli(String... args) {
        try {
            ProcessOutput output = ExecUtil.execAndGetOutput(new GeneralCommandLine(args));
            List<String> lines = output.getStdoutLines();
            return lines.isEmpty() ? Optional.empty() : Optional.of(lines.get(0));
        } catch (ExecutionException e) {
            Logger.PLUGIN.error("Failed to execute [%s]".formatted(String.join(" ", args)), e);
            return Optional.empty();
        }
    }
}
