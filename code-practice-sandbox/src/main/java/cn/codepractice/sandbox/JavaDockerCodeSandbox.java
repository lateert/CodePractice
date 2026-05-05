package cn.codepractice.sandbox;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.ArrayUtil;
import cn.codepractice.sandbox.model.ExecuteMessage;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class JavaDockerCodeSandbox extends CodeSandboxTemplate {

    public static boolean FIRST_INIT = false;

    @Override
    protected List<ExecuteMessage> runFile(File userCodeFile, List<String> inputList) {

        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();

        // Только Docker: TCP endpoint, удобный для Docker Desktop на Windows.
        // Приоритет: переменная DOCKER_HOST, иначе tcp://localhost:2375
        String dockerHost = System.getenv("DOCKER_HOST");
        if (dockerHost == null || dockerHost.trim().isEmpty()) {
            dockerHost = "tcp://localhost:2375";
        }

        DockerClient dockerClient;
        try {
            DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                    .withDockerHost(dockerHost)
                    .build();
            DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                    .dockerHost(config.getDockerHost())
                    .sslConfig(config.getSSLConfig())
                    .build();
            dockerClient = DockerClientImpl.getInstance(config, httpClient);
            // Проверяем доступность daemon сразу, чтобы упасть с понятной ошибкой
            dockerClient.pingCmd().exec();
        } catch (Exception e) {
            throw new RuntimeException("Не удалось подключиться к Docker daemon по " + dockerHost
                    + ". Включите Docker Desktop и опцию TCP 2375 (Settings > General > Expose daemon on tcp://localhost:2375 without TLS).", e);
        }

        String image = "xldevops/jdk17-alpine";

        if (FIRST_INIT) {
            PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);
            PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
                @Override
                public void onNext(PullResponseItem item) {
                    System.out.println("Pull image: " + item.getStatus());
                    super.onNext(item);
                }
            };
            try {
                pullImageCmd.exec(pullImageResultCallback)
                        .awaitCompletion();
                FIRST_INIT = false;
            } catch (InterruptedException e) {
                System.out.println("Pull image interrupted");
                throw new RuntimeException(e);
            }

            System.out.println("Image pull finished");
        }

        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image);
        HostConfig hostConfig = new HostConfig();
        hostConfig.setBinds(new Bind(userCodeParentPath, new Volume("/app")));
        hostConfig.withMemory(100 * 1000 * 1000L);
        hostConfig.withCpuCount(1L);
        hostConfig.withMemorySwap(0L);
        hostConfig.withReadonlyRootfs(true);

        CreateContainerResponse createContainerResponse = containerCmd
                .withHostConfig(hostConfig)
                .withNetworkDisabled(true)
                .withAttachStdin(true)
                .withAttachStderr(true)
                .withAttachStdout(true)
                .withTty(true)
                .exec();
        System.out.println(createContainerResponse);
        String containerId = createContainerResponse.getId();

        dockerClient.startContainerCmd(containerId).exec();


        List<ExecuteMessage> executeMessageList = new ArrayList<>();

        // docker exec ... java -cp /app Main 1 3
        for (String inputArgs : inputList) {
            StopWatch stopWatch = new StopWatch();
            String[] inputArgsArray = inputArgs.split(" ");
            // Передаём данные и как аргументы, и через stdin.
            // Команда будет такой: sh -c "echo '1 2' | java -cp /app Main 1 2"
            String cmd = String.format("echo '%s' | java -cp /app Main %s", inputArgs, inputArgs);
            String[] cmdArray = new String[] {"sh", "-c", cmd};
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withCmd(cmdArray)
                    .withAttachStdin(true)
                    .withAttachStderr(true)
                    .withAttachStdout(true)
                    .exec();
            System.out.println("exec create: " + execCreateCmdResponse);

            ExecuteMessage executeMessage = new ExecuteMessage();
            final String[] message = {null};
            final String[] errorMessage = {null};
            long time = 0L;
            final boolean[] timeout = {true};

            String execId = execCreateCmdResponse.getId();

            ExecStartResultCallback resultCallback = new ExecStartResultCallback() {

                @Override
                public void onComplete() {
                    timeout[0] = false;
                    super.onComplete();
                }

                @Override
                public void onNext(Frame frame) {
                    StreamType streamType = frame.getStreamType();
                    if (StreamType.STDERR.equals(streamType)) {
                        errorMessage[0] = new String(frame.getPayload());
                        System.out.println("stderr: " + errorMessage[0]);
                    } else {
                        message[0] = new String(frame.getPayload());
                        System.out.println("stdout: " + message[0]);
                    }
                    super.onNext(frame);
                }
            };

            final long[] maxMemory = {0L};
            StatsCmd statsCmd = dockerClient.statsCmd(containerId);
            ResultCallback<Statistics> statisticsResultCallback = statsCmd.exec(new ResultCallback<Statistics>() {
                @Override
                public void onStart(Closeable closeable) {

                }

                @Override
                public void onNext(Statistics statistics) {
                    System.out.println("memory usage: " + statistics.getMemoryStats().getUsage());
                    maxMemory[0] = Math.max(statistics.getMemoryStats().getUsage(), maxMemory[0]);
                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onComplete() {

                }

                @Override
                public void close() throws IOException {

                }
            });

            try {
                stopWatch.start();
                dockerClient.execStartCmd(execId)
                        .exec(resultCallback)
                        .awaitCompletion(5, TimeUnit.SECONDS);
                stopWatch.stop();
                time = stopWatch.getLastTaskTimeMillis();
                statsCmd.close();
                statisticsResultCallback.close();
            } catch (InterruptedException e) {
                System.out.println("exec interrupted / failed");
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // stdout / stderr могут быть пустыми, поэтому защищаемся от null
            String stdout = message[0];
            String stderr = errorMessage[0];

            if (stdout != null) {
                // Нормализуем переносы строк, но не склеиваем вывод в одну строку.
                executeMessage.setMessage(stdout.replace("\r\n", "\n").replace("\r", ""));
            } else {
                executeMessage.setMessage("");
            }

            if (stderr != null) {
                executeMessage.setErrorMessage(stderr);
            } else {
                executeMessage.setErrorMessage("");
            }
            executeMessage.setTime(time);
            executeMessage.setMemory(maxMemory[0]);
            executeMessageList.add(executeMessage);
        }

        StopContainerCmd stopContainerCmd = dockerClient.stopContainerCmd(containerId);
        stopContainerCmd.exec();
        RemoveContainerCmd removeContainerCmd = dockerClient.removeContainerCmd(containerId);
        removeContainerCmd.exec();

        return executeMessageList;
    }
}
