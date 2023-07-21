package aml.summereureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class SummerEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SummerEurekaApplication.class, args);
    }

}
