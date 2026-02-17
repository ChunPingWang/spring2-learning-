package com.mes.testing.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Random;

@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;
    private final Random random = new Random();

    public DatabaseHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {
        try (Connection conn = dataSource.getConnection()) {
            boolean isValid = conn.isValid(2);
            
            if (isValid) {
                return Health.up()
                        .withDetail("database", "H2 Memory")
                        .withDetail("validationQuery", "OK")
                        .withDetail("autoCommit", conn.getAutoCommit())
                        .build();
            } else {
                return Health.down()
                        .withDetail("database", "H2 Memory")
                        .withDetail("validationQuery", "FAILED")
                        .build();
            }
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
