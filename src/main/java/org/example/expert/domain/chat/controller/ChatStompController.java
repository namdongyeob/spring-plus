package org.example.expert.domain.chat.controller;

import java.security.Principal;

import org.example.expert.config.StompPrincipal;
import org.example.expert.domain.chat.dto.request.ChatMessageSendRequest;
import org.example.expert.domain.chat.dto.response.ChatMessageResponse;
import org.example.expert.domain.chat.service.ChatMessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatStompController {

	private final ChatMessageService chatMessageService;
	private final SimpMessagingTemplate messagingTemplate;

	@MessageMapping("/chat.send")
	public void send(ChatMessageSendRequest request, Principal principal) {
		StompPrincipal stompPrincipal = (StompPrincipal)principal;

		ChatMessageResponse response = chatMessageService.saveMessage(
			stompPrincipal.getUserId(), stompPrincipal.getNickname(), request);
		messagingTemplate.convertAndSend("/sub/chat/" + request.getRoomId(), response);
	}
}
