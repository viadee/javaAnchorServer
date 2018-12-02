package de.viadee.anchorj.server.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = { "de.viadee.anchorj.server" })
@ComponentScan(basePackages = { "de.viadee.anchorj.server" })
public class AnchorServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnchorServerApplication.class, args);
    }

}
