package org.example.expert.domain.chat.controller;

import org.example.expert.domain.chat.dto.request.ChatRoomCreateRequest;
import org.example.expert.domain.chat.dto.response.ChatRoomResponse;
import org.example.expert.domain.chat.service.ChatRoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

	private final ChatRoomService chatRoomService;

	@PostMapping
	public ResponseEntity<ChatRoomResponse> create(
		@RequestBody ChatRoomCreateRequest request) {
		return ResponseEntity.ok(chatRoomService.createRoom(request));
	}
}
