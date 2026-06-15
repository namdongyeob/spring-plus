package org.example.expert.config;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

@ExtendWith(MockitoExtension.class)
class StompAuthInterceptorTest {

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private UserRepository userRepository;

	@Mock
	private MessageChannel channel;

	@Mock
	private Claims claims;

	@Test
	void preSend_throwsAuthExceptionWhenAuthorizationHeaderMissing() {
		StompAuthInterceptor interceptor = new StompAuthInterceptor(jwtUtil, userRepository);

		assertThatThrownBy(() -> interceptor.preSend(connectMessage(null), channel))
			.isInstanceOf(AuthException.class)
			.hasMessage("WebSocket 인증 토큰이 없습니다.");
	}

	@Test
	void preSend_throwsAuthExceptionWhenJwtInvalid() {
		StompAuthInterceptor interceptor = new StompAuthInterceptor(jwtUtil, userRepository);
		when(jwtUtil.substringToken("Bearer wrong-token")).thenReturn("wrong-token");
		when(jwtUtil.extractClaims("wrong-token")).thenThrow(new JwtException("bad token"));

		assertThatThrownBy(() -> interceptor.preSend(connectMessage("Bearer wrong-token"), channel))
			.isInstanceOf(AuthException.class)
			.hasMessage("유효하지 않은 WebSocket 인증 토큰입니다.");
	}

	@Test
	void preSend_throwsAuthExceptionWhenUserNotFound() {
		StompAuthInterceptor interceptor = new StompAuthInterceptor(jwtUtil, userRepository);
		when(jwtUtil.substringToken("Bearer valid-token")).thenReturn("valid-token");
		when(jwtUtil.extractClaims("valid-token")).thenReturn(claims);
		when(claims.getSubject()).thenReturn("1");
		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> interceptor.preSend(connectMessage("Bearer valid-token"), channel))
			.isInstanceOf(AuthException.class)
			.hasMessage("WebSocket 인증 유저를 찾을 수 없습니다.");
	}

	private Message<byte[]> connectMessage(String authorization) {
		StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
		if (authorization != null) {
			accessor.setNativeHeader("Authorization", authorization);
		}
		return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
	}
}
