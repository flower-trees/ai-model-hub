package org.salt.ai.hub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@SpringBootApplication
@EnableAsync
public class ServiceApplication {
    public static void main(String[] args) {
        try {
            ConfigurableApplicationContext applicationContext =
                    SpringApplication.run(ServiceApplication.class, args);

            Environment env = applicationContext.getEnvironment();
            log.info("\n----------------------------------------------------------" +
                            "\n\t Service '{}' start successfully!" +
                            "\n\t environment:     {}" +
                            "\n\t access address:  http://127.0.0.1:{}{}" +
                            "\n\t api document:    http://127.0.0.1:{}{}/swagger-ui.html" +
                            "\n----------------------------------------------------------",
                    env.getProperty("spring.application.name"),
                    env.getActiveProfiles(),
                    env.getProperty("server.port"),
                    env.getProperty("server.servlet.context-path"),
                    env.getProperty("server.port"),
                    env.getProperty("server.servlet.context-path")
            );
        } catch (Exception e) {
//            System.out.println(e);
//            throw new RuntimeException("Spring container start failed! ", e);
        }
    }
}
