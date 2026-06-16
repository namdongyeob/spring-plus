package org.example.expert.domain.user.controller;

import org.example.expert.config.JwtUtil;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named = "USER_SEARCH_PERFORMANCE_TEST", matches = "true")
class UserSearchPerformanceTest {

    private static final String SAMPLE_NICKNAME = "bulk-20260616095005-0500000";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @WithMockUser
    void nickname_exact_match_search_time() throws Exception {
        String bearerToken = jwtUtil.createToken(1L, "performance@test.com", UserRole.USER, "performance");

        long startNanos = System.nanoTime();
        MvcResult result = mockMvc.perform(get("/users")
                        .header("Authorization", bearerToken)
                        .param("nickname", SAMPLE_NICKNAME))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nickname").value(SAMPLE_NICKNAME))
                .andReturn();
        long elapsedMicros = (System.nanoTime() - startNanos) / 1_000;

        System.out.printf(
                "User nickname search took %,d microseconds. nickname=%s, response=%s%n",
                elapsedMicros,
                SAMPLE_NICKNAME,
                result.getResponse().getContentAsString()
        );

        printExplainPlan();
    }

    private void printExplainPlan() {
        Map<String, Object> explain = jdbcTemplate.queryForMap(
                "EXPLAIN SELECT * FROM users WHERE nickname = ?",
                SAMPLE_NICKNAME
        );

        System.out.printf(
                "EXPLAIN users nickname search: type=%s, key=%s, rows=%s%n",
                explain.get("type"),
                explain.get("key"),
                explain.get("rows")
        );
    }
}
