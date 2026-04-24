package com.billbharat.sales;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main entry point for the BillBharat Sales Backend application.
 * Provides REST APIs for the BillBharat Sales React Native app.
 */
@SpringBootApplication
@EnableJpaAuditing
public class BillBharatSalesApplication {

    public static void main(String[] args) {
        SpringApplication.run(BillBharatSalesApplication.class, args);
    }
}
