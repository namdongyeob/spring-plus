package org.example.expert.config;

import java.security.Principal;

import org.example.expert.domain.common.dto.AuthUser;

import lombok.Getter;

@Getter
public class StompPrincipal implements Principal {

	private final String name;       // STOMP Principal 식별자 (인증: userId, 익명: anon-xxxx)
	private final AuthUser authUser; // 익명일 경우 null (인증 유저만 권한/role 보유)
	private final String nickname;   // 메시지에 스냅샷으로 저장할 표시명

	private StompPrincipal(String name, AuthUser authUser, String nickname) {
		this.name = name;
		this.authUser = authUser;
		this.nickname = nickname;
	}

	public static StompPrincipal authenticated(AuthUser authUser, String nickname) {
		return new StompPrincipal(String.valueOf(authUser.getId()), authUser, nickname);
	}

	public static StompPrincipal anonymous(String name, String nickname) {
		return new StompPrincipal(name, null, nickname);
	}

	public boolean isAnonymous() {
		return authUser == null;
	}

	public Long getUserId() {
		return authUser == null ? null : authUser.getId();
	}

	@Override
	public String getName() {
		return name;
	}
}
