package org.example.expert.domain.chat.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.example.expert.domain.chat.dto.response.StompErrorResponse;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.junit.jupiter.api.Test;

class ChatStompExceptionHandlerTest {

	private final ChatStompExceptionHandler handler = new ChatStompExceptionHandler();

	@Test
	void handleInvalidRequest_returnsChatMessageErrorResponse() {
		StompErrorResponse response = handler.handleInvalidRequest(
			new InvalidRequestException("채팅방을 찾을 수 없습니다.")
		);

		assertThat(response.getType()).isEqualTo("CHAT_MESSAGE_ERROR");
		assertThat(response.getMessage()).isEqualTo("채팅방을 찾을 수 없습니다.");
	}
}
