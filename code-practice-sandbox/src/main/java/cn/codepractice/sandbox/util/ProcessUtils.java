package cn.codepractice.sandbox.util;

import cn.hutool.core.util.StrUtil;
import cn.codepractice.sandbox.model.ExecuteMessage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/** Чтение stdout/stderr дочернего процесса в модель ответа. */
public class ProcessUtils {

    /**
     * Ожидает завершение процесса; при успехе — stdout, при ошибке — stdout и stderr в {@link ExecuteMessage}.
     *
     * @param runProcess процесс ОС (компиляция или запуск)
     * @param opName     метка для логов (например compile)
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
