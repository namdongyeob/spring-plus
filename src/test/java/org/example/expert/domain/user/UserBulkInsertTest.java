package org.example.expert.domain.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@EnabledIfEnvironmentVariable(named = "BULK_INSERT_USERS", matches = "true")
class UserBulkInsertTest {

    private static final int TOTAL_COUNT = 1_000_000;
    private static final int BATCH_SIZE = 5_000;
    private static final String JDBC_URL =
            "jdbc:mysql://localhost:3306/spring_plus?rewriteBatchedStatements=true";
    private static final String ENCODED_PASSWORD =
            "$2a$10$yU3HXXGsw0q6naTIQD1jS.nmkdRYuLYFRhzG19UvbR7PKXSfk8M9a";

    @Test
    void jdbc_bulk_insert_creates_1_000_000_users() {
        JdbcTemplate jdbcTemplate = jdbcTemplate();
        String runKey = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        LocalDateTime now = LocalDateTime.now();
        long beforeCount = countUsers(jdbcTemplate);

        long startNanos = System.nanoTime();
        for (int offset = 0; offset < TOTAL_COUNT; offset += BATCH_SIZE) {
            int batchCount = Math.min(BATCH_SIZE, TOTAL_COUNT - offset);
            insertBatch(jdbcTemplate, runKey, now, offset, batchCount);
        }
        long elapsedMillis = (System.nanoTime() - startNanos) / 1_000_000;

        long afterCount = countUsers(jdbcTemplate);
        String sampleNickname = nickname(runKey, TOTAL_COUNT / 2);

        System.out.printf(
                "Inserted %,d users in %,d ms. sampleNickname=%s%n",
                TOTAL_COUNT,
                elapsedMillis,
                sampleNickname
        );

        assertThat(afterCount).isEqualTo(beforeCount + TOTAL_COUNT);
    }

    private JdbcTemplate jdbcTemplate() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(JDBC_URL);
        dataSource.setUsername(requiredEnv("MYSQL_USERNAME"));
        dataSource.setPassword(requiredEnv("MYSQL_PASSWORD"));

        return new JdbcTemplate(dataSource);
    }

    private String requiredEnv(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(name + " environment variable is required.");
        }
        return value;
    }

    private void insertBatch(JdbcTemplate jdbcTemplate, String runKey, LocalDateTime now, int offset, int batchCount) {
        String sql = """
                INSERT INTO users (created_at, modified_at, email, password, user_role, nickname)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int index) throws SQLException {
                int userNumber = offset + index + 1;
                Timestamp timestamp = Timestamp.valueOf(now);

                ps.setTimestamp(1, timestamp);
                ps.setTimestamp(2, timestamp);
                ps.setString(3, email(runKey, userNumber));
                ps.setString(4, ENCODED_PASSWORD);
                ps.setString(5, "USER");
                ps.setString(6, nickname(runKey, userNumber));
            }

            @Override
            public int getBatchSize() {
                return batchCount;
            }
        });
    }

    private long countUsers(JdbcTemplate jdbcTemplate) {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Long.class);
        return count == null ? 0 : count;
    }

    private String email(String runKey, int userNumber) {
        return "bulk-%s-%07d@example.com".formatted(runKey, userNumber);
    }

    private String nickname(String runKey, int userNumber) {
        return "bulk-%s-%07d".formatted(runKey, userNumber);
    }
}
