package org.example.expert.domain.chat.service;

import org.example.expert.domain.chat.dto.request.ChatRoomCreateRequest;
import org.example.expert.domain.chat.dto.response.ChatRoomResponse;
import org.example.expert.domain.chat.entity.ChatRoom;
import org.example.expert.domain.chat.repository.ChatRoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

	private final ChatRoomRepository chatRoomRepository;

	@Transactional
	public ChatRoomResponse createRoom(ChatRoomCreateRequest request) {
		ChatRoom chatRoom = new ChatRoom(request.getName());
		ChatRoom savedRoom = chatRoomRepository.save(chatRoom);
		return new ChatRoomResponse(savedRoom);

	}
}
