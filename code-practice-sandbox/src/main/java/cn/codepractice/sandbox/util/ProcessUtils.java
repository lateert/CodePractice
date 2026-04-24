package cn.codepractice.sandbox.util;

import cn.hutool.core.util.StrUtil;
import cn.codepractice.sandbox.model.ExecuteMessage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/** Helpers to drain stdout/stderr from a {@link Process}. */
public class ProcessUtils {

    /**
     * Wait for the process, then capture stdout (and stderr on failure) into an {@link ExecuteMessage}.
     *
     * @param runProcess OS process (compile or run)
     * @param opName      label for logs (e.g. {@code compile})
     */
    public static ExecuteMessage runProcessAndGetMessage(Process runProcess, String opName) {
        ExecuteMessage executeMessage = new ExecuteMessage();

        try {
            int exitValue = runProcess.waitFor();
            executeMessage.setExitValue(exitValue);
            if (exitValue == 0) {
                System.out.println(opName + " OK");
                BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
                List<String> outputStrList = new ArrayList<>();
                String compileOutputLine;
                while ((compileOutputLine = bufferedReader.readLine()) != null) {
                    outputStrList.add(compileOutputLine);
                }
                executeMessage.setMessage(StrUtil.join("\n", outputStrList));
            } else {
                System.out.println(opName + " failed, exit code: " + exitValue);
                BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
                List<String> errorOutputStrList = new ArrayList<>();
                String compileOutputLine;
                while ((compileOutputLine = bufferedReader.readLine()) != null) {
                    errorOutputStrList.add(compileOutputLine);
                }
                executeMessage.setMessage(StrUtil.join("\n", errorOutputStrList));

                BufferedReader errorBufferedReader =
                        new BufferedReader(new InputStreamReader(runProcess.getErrorStream()));
                StringBuilder errorCompileOutputStringBuilder = new StringBuilder();

                String errorCompileOutputLine;
                while ((errorCompileOutputLine = errorBufferedReader.readLine()) != null) {
                    errorCompileOutputStringBuilder.append(errorCompileOutputLine);
                }
                executeMessage.setErrorMessage(errorCompileOutputStringBuilder.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return executeMessage;
    }
}
