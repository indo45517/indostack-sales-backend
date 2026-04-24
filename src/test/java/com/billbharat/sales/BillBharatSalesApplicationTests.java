package com.billbharat.sales;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.liquibase.enabled=false",
        "jwt.secret=test-secret-key-that-is-long-enough-for-testing-purposes-only",
        "jwt.expiration=86400000",
        "jwt.refresh-expiration=604800000"
})
class BillBharatSalesApplicationTests {

    @Test
    void contextLoads() {
        // Verify the Spring application context loads successfully
    }
}
