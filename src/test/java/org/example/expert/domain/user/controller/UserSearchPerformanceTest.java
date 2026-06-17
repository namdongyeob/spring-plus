package org.example.expert.domain.user.controller;

import org.example.expert.config.JwtUtil;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named = "USER_SEARCH_PERFORMANCE_TEST", matches = "true")
class UserSearchPerformanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @WithMockUser
    void nickname_exact_match_search_time() throws Exception {
        assumeRedisResponds();

        // DB에 실제 존재하는 닉네임을 동적으로 선택해 재현성을 확보한다.
        String sampleNickname = jdbcTemplate.queryForObject(
                "SELECT nickname FROM users WHERE nickname LIKE 'bulk-%' ORDER BY id LIMIT 1",
                String.class
        );

        String bearerToken = jwtUtil.createToken(1L, "performance@test.com", UserRole.USER, "performance");

        // 1회차 - 캐시 미스 (DB 조회, 커버링 인덱스 사용)
        long coldStart = System.nanoTime();
        MvcResult result = mockMvc.perform(get("/users")
                        .header("Authorization", bearerToken)
                        .param("nickname", sampleNickname))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nickname").value(sampleNickname))
                .andReturn();
        long coldMicros = (System.nanoTime() - coldStart) / 1_000;

        // 2회차 - 캐시 히트 (Redis 반환, DB 우회)
        long warmStart = System.nanoTime();
        mockMvc.perform(get("/users")
                        .header("Authorization", bearerToken)
                        .param("nickname", sampleNickname))
                .andExpect(status().isOk());
        long warmMicros = (System.nanoTime() - warmStart) / 1_000;

        System.out.printf(
                "User nickname search: [cache miss] %,d us, [cache hit] %,d us. nickname=%s, response=%s%n",
                coldMicros,
                warmMicros,
                sampleNickname,
                result.getResponse().getContentAsString()
        );

        printExplainPlan(sampleNickname);
    }

    private void printExplainPlan(String nickname) {
        Map<String, Object> explain = jdbcTemplate.queryForMap(
                "EXPLAIN SELECT id, email, nickname FROM users WHERE nickname = ?",
                nickname
        );

        System.out.printf(
                "EXPLAIN(covering projection): type=%s, key=%s, Extra=%s, rows=%s%n",
                explain.get("type"),
                explain.get("key"),
                explain.get("Extra"),
                explain.get("rows")
        );
    }

    private void assumeRedisResponds() {
        Assumptions.assumeTrue(
                redisRespondsToPing(),
                "Redis must respond to PING on localhost:6379. Start Redis before running this performance test."
        );
    }

    private boolean redisRespondsToPing() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("localhost", 6379), 1_000);
            socket.setSoTimeout(1_000);

            OutputStream outputStream = socket.getOutputStream();
            outputStream.write("*1\r\n$4\r\nPING\r\n".getBytes(StandardCharsets.US_ASCII));
            outputStream.flush();

            InputStream inputStream = socket.getInputStream();
            byte[] buffer = new byte[16];
            int length = inputStream.read(buffer);
            String response = length <= 0 ? "" : new String(buffer, 0, length, StandardCharsets.US_ASCII);

            return response.startsWith("+PONG");
        } catch (IOException e) {
            return false;
        }
    }
}
