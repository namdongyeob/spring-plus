package org.example.expert.domain.chat.controller;

import org.example.expert.domain.chat.dto.response.StompErrorResponse;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ChatStompExceptionHandler {

	@MessageExceptionHandler(InvalidRequestException.class)
	@SendToUser("/queue/errors")
	public StompErrorResponse handleInvalidRequest(InvalidRequestException ex) {
		return new StompErrorResponse("CHAT_MESSAGE_ERROR", ex.getMessage());
	}
}
