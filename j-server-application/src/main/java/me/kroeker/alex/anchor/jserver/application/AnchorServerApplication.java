package me.kroeker.alex.anchor.jserver.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = { "me.kroeker.alex.anchor.jserver" })
@ComponentScan(basePackages = { "me.kroeker.alex.anchor.jserver" })
public class AnchorServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnchorServerApplication.class, args);
    }

}
