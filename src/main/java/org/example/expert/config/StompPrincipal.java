package org.example.expert.config;

import java.security.Principal;

import org.example.expert.domain.common.dto.AuthUser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StompPrincipal implements Principal {

	private final AuthUser authUser;
	private final String nickname;

	@Override
	public String getName() {
		return String.valueOf(authUser.getId());
	}
}
