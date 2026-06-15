package org.example.expert.config;

import java.util.Objects;

import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompAuthInterceptor implements ChannelInterceptor {

	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;

	@Override
	public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {

		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

		if (StompCommand.CONNECT.equals(Objects.requireNonNull(accessor).getCommand())) {

			String bearerToken = accessor.getFirstNativeHeader("Authorization");
			if (!StringUtils.hasText(bearerToken) || !bearerToken.startsWith("Bearer ")) {
				log.warn("STOMP CONNECT rejected: missing Authorization header");
				throw new AuthException("WebSocket 인증 토큰이 없습니다.");
			}

			String token = jwtUtil.substringToken(bearerToken);

			Claims claims;
			Long userId;
			try {
				claims = jwtUtil.extractClaims(token);
				userId = Long.parseLong(claims.getSubject());
			} catch (JwtException | IllegalArgumentException e) {
				log.warn("STOMP CONNECT rejected: invalid JWT", e);
				throw new AuthException("유효하지 않은 WebSocket 인증 토큰입니다.");
			}

			User user = userRepository.findById(userId)
				.orElseThrow(() -> {
					log.warn("STOMP CONNECT rejected: user not found, userId={}", userId);
					return new AuthException("WebSocket 인증 유저를 찾을 수 없습니다.");
				});

			AuthUser authUser = new AuthUser(user.getId(), user.getEmail(), user.getUserRole());
			accessor.setUser(new StompPrincipal(authUser, user.getNickname()));
		}

		return message;
	}
}
