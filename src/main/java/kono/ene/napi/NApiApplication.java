package kono.ene.napi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class})
public class NApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(NApiApplication.class, args);
    }
}
