package org.example.expert.domain.chat.controller;

import java.util.List;

import org.example.expert.domain.chat.dto.response.ChatMessageResponse;
import org.example.expert.domain.chat.service.ChatMessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatMessageQueryController {

	private final ChatMessageService chatMessageService;

	@GetMapping("/rooms/{roomId}/messages")
	public List<ChatMessageResponse> getMessages(
		@PathVariable Long roomId,
		@RequestParam(defaultValue = "50") int size) {
		return chatMessageService.getRecentMessages(roomId, size);
	}
}
