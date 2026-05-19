package com.prasadfencing.backendecom;

import com.prasadfencing.backendecom.payment.config.RazorpayConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication
public class BackendEcomApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendEcomApplication.class, args);
    }

}
