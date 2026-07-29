package com.ratemyrental;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
    title = "RateMyRental API",
    version = "1.0.0",
    description = "Backend API for RateMyRental application"
))
public class RateMyRentalApplication {

    public static void main(String[] args) {
        SpringApplication.run(RateMyRentalApplication.class, args);
    }

}
