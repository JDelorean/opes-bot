package pl.jdev.opes_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableAutoConfiguration
public class OpesBot extends SpringBootServletInitializer {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(OpesBot.class, args);
    }
}
