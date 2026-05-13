package cn.codepractice.sandbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.codepractice.sandbox.model.ExecuteCodeRequest;
import cn.codepractice.sandbox.model.ExecuteCodeResponse;
import cn.codepractice.sandbox.model.ExecuteMessage;
import cn.codepractice.sandbox.model.JudgeInfo;
import cn.codepractice.sandbox.util.ProcessUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Общие шаги компиляции, запуска и очистки для нативной песочницы. */
public abstract class CodeSandboxTemplate implements CodeSandbox {

    private static final String GLOBAL_CODE_DIR_NAME = "tempCode";

    private static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";

    private static final long TIME_OUT = 5000L;

    /** Сохраняет исходник пользователя во временный каталог на один запуск. */
    protected File saveCodeToFile(String code) {

        String userDir = System.getProperty("user.dir");
        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;

        if (!FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }

        String userCodeParentPath = globalCodePathName + File.separator + UUID.randomUUID();
        String userCodePath = userCodeParentPath + File.separator + GLOBAL_JAVA_CLASS_NAME;
        return FileUtil.writeUtf8String(code, userCodePath);
    }

    /** Запуск javac для сохранённого файла. */
    protected ExecuteMessage compileFile(File userCodeFile) {
        String compileCmd = String.format("javac -encoding utf-8 --release 11 %s", userCodeFile.getAbsoluteFile());

        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(compileProcess, "compile");
            System.out.println("executeMessage = " + executeMessage);
            if (executeMessage.getExitValue() != 0) {
                throw new RuntimeException("Compilation failed");
            }
            return executeMessage;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /** Запуск скомпилированного Main для каждой строки входных данных из inputList. */
    protected List<ExecuteMessage> runFile(File userCodeFile, List<String> inputList) {
        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();

        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        for (String inputArgs : inputList) {
            // Передаём данные и как аргументы, и через stdin,
            // чтобы поддерживать оба варианта решений.
            String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main %s", userCodeParentPath, inputArgs);
            try {
                Process runProcess = Runtime.getRuntime().exec(runCmd);
                // Передаём тестовый ввод в stdin
                try {
                    runProcess.getOutputStream().write((inputArgs + System.lineSeparator()).getBytes());
                    runProcess.getOutputStream().flush();
                    runProcess.getOutputStream().close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // Таймаут выполнения
                new Thread(() -> {
                    try {
                        Thread.sleep(TIME_OUT);
                        System.out.println("Timeout, destroying process");
                        runProcess.destroy();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();

                ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(runProcess, "run");
                System.out.println(executeMessage);
                executeMessageList.add(executeMessage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return executeMessageList;
    }

    /** Собирает вывод по тест-кейсам в ответ API и статус проверки. */
    protected ExecuteCodeResponse getOutputResponse(List<ExecuteMessage> executeMessageList) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        ArrayList<String> outputList = new ArrayList<>();

        long maxTime = 0L;
        long maxMemory = 0L;
        for (ExecuteMessage executeMessage : executeMessageList) {
            String errorMessage = executeMessage.getErrorMessage();
            if (StrUtil.isNotBlank(errorMessage)) {
                executeCodeResponse.setMessage(errorMessage);
                executeCodeResponse.setStatus(3);
                break;
            }
            outputList.add(executeMessage.getMessage());
            Long time = executeMessage.getTime();
            if (time != null) {
                maxTime = Math.max(maxTime, time);
            }
            Long memory = executeMessage.getMemory();
            if (memory != null) {
                maxMemory = Math.max(maxMemory, memory);
            }
        }
        if (outputList.size() == executeMessageList.size()) {
            executeCodeResponse.setStatus(2);
        }
        executeCodeResponse.setOutput(outputList);
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setTime(maxTime);
        judgeInfo.setMemory(maxMemory);

        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }

    /** Удаляет временный каталог с исходниками пользователя. */
    protected boolean deleteFile(File userCodeFile) {
        if (userCodeFile.getParentFile() != null) {
            String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();
            boolean del = FileUtil.del(userCodeParentPath);
            System.out.println("Delete temp dir: " + (del ? "OK" : "FAILED"));
            return del;
        }
        return true;
    }

    private ExecuteCodeResponse getErrorResponse(Throwable e) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutput(new ArrayList<>());
        executeCodeResponse.setMessage(e.getMessage());

        executeCodeResponse.setStatus(2);
        executeCodeResponse.setJudgeInfo(new JudgeInfo());
        return executeCodeResponse;
    }


    @Override
    public ExecuteCodeResponse doExecute(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInput();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();

        File file = saveCodeToFile(code);

        ExecuteMessage executeMessage = compileFile(file);

        List<ExecuteMessage> executeMessageList = runFile(file, inputList);

        ExecuteCodeResponse outputResponse = getOutputResponse(executeMessageList);

        boolean b = deleteFile(file);
        if (!b) {
            System.out.println("Failed to delete temp files");
        }

        return outputResponse;
    }
}
