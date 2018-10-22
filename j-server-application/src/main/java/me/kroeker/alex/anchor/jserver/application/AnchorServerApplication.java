package me.kroeker.alex.anchor.jserver.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "me.kroeker.alex.anchor.jserver")
public class AnchorServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnchorServerApplication.class, args);
    }
}
