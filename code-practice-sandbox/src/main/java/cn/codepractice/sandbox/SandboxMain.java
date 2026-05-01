package cn.codepractice.sandbox;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Точка входа сервиса песочницы (Spring Boot). */
@SpringBootApplication
public class SandboxMain {
    public static void main(String[] args) {
        SpringApplication.run(SandboxMain.class, args);
        System.out.println("Hello world!");
    }
}