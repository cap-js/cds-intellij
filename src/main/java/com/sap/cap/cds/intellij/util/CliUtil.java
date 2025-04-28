package com.sap.cap.cds.intellij.util;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

public class CliUtil {
    public static Optional<String> executeCli(String... args) {
        try {
            Process process = new GeneralCommandLine(args).createProcess();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                return Optional.ofNullable(reader.readLine());
            }
        } catch (ExecutionException | IOException e) {
            Logger.PLUGIN.error("Failed to execute [%s]".formatted(String.join(" ", args)), e);
            return Optional.empty();
        }
    }
}
