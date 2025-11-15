package isa.vezbe2.scheduled_example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/*
 * Ukljucivanje podrske za raspored izvrsavanja.
 */

@SpringBootApplication
@EnableScheduling
public class ScheduledExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScheduledExampleApplication.class, args);
    }

}