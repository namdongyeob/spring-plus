// 인증(401)·인가(403) 실패를 GlobalExceptionHandler와 동일한 JSON 포맷으로 응답하는 핸들러
package org.example.expert.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SecurityExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	// 인증되지 않은 요청 (토큰 없음 등) → 401
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException {
		writeError(response, HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");
	}

	// 인증은 됐으나 권한 부족 (USER가 /admin/** 접근 등) → 403
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws IOException {
		writeError(response, HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
	}

	private void writeError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
		response.setStatus(status.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());

		Map<String, Object> body = new HashMap<>();
		body.put("status", status.name());
		body.put("code", status.value());
		body.put("message", message);

		objectMapper.writeValue(response.getWriter(), body);
	}
}
