package org.example.expert.domain.chat.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StompErrorResponse {

	private final String type;
	private final String message;
}
