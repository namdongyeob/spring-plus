// 인증(401)·인가(403) 실패 응답이 GlobalExceptionHandler와 동일한 JSON 포맷으로 나가는지 검증
package org.example.expert.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import com.fasterxml.jackson.databind.ObjectMapper;

class SecurityExceptionHandlerTest {

	private final SecurityExceptionHandler handler = new SecurityExceptionHandler();
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void commence_writesUnauthorizedJson() throws Exception {
		MockHttpServletResponse response = new MockHttpServletResponse();

		handler.commence(new MockHttpServletRequest(), response, new BadCredentialsException("no auth"));

		assertThat(response.getStatus()).isEqualTo(401);
		assertThat(response.getContentType()).contains("application/json");

		Map<?, ?> body = objectMapper.readValue(response.getContentAsString(), Map.class);
		assertThat(body.get("status")).isEqualTo("UNAUTHORIZED");
		assertThat(body.get("code")).isEqualTo(401);
		assertThat(body.get("message")).isEqualTo("인증이 필요합니다.");
	}

	@Test
	void handle_writesForbiddenJson() throws Exception {
		MockHttpServletResponse response = new MockHttpServletResponse();

		handler.handle(new MockHttpServletRequest(), response, new AccessDeniedException("denied"));

		assertThat(response.getStatus()).isEqualTo(403);
		assertThat(response.getContentType()).contains("application/json");

		Map<?, ?> body = objectMapper.readValue(response.getContentAsString(), Map.class);
		assertThat(body.get("status")).isEqualTo("FORBIDDEN");
		assertThat(body.get("code")).isEqualTo(403);
		assertThat(body.get("message")).isEqualTo("접근 권한이 없습니다.");
	}
}
