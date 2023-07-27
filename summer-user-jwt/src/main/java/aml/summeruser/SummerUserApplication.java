package aml.summeruser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication(scanBasePackages = "aml.summeruser")
@EnableEurekaClient
@EnableJpaRepositories
//@EnableKafka
public class SummerUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(SummerUserApplication.class, args);
    }

}
