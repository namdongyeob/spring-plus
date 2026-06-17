package org.example.expert.domain.chat.service;

import java.util.List;

import org.example.expert.domain.chat.dto.request.ChatMessageSendRequest;
import org.example.expert.domain.chat.dto.response.ChatMessageResponse;
import org.example.expert.domain.chat.entity.ChatMessage;
import org.example.expert.domain.chat.entity.ChatRoom;
import org.example.expert.domain.chat.repository.ChatMessageRepository;
import org.example.expert.domain.chat.repository.ChatRoomRepository;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageService {

	private final ChatMessageRepository chatMessageRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final UserRepository userRepository;

	@Transactional
	public ChatMessageResponse saveMessage(Long userId, String senderName, ChatMessageSendRequest request) {
		ChatRoom chatRoom = chatRoomRepository.findById(request.getRoomId())
			.orElseThrow(() -> new InvalidRequestException("채팅방을 찾을 수 없습니다."));

		User sender = null;
		if (userId != null) {
			sender = userRepository.findById(userId)
				.orElseThrow(() -> new InvalidRequestException("유저를 찾을 수 없습니다."));
		}

		ChatMessage chatMessage = new ChatMessage(chatRoom, sender, senderName, request.getContent());
		ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

		return new ChatMessageResponse(savedMessage);
	}

	public List<ChatMessageResponse> getRecentMessages(Long roomId, int size) {
		Pageable pageable = PageRequest.of(0, size);

		return chatMessageRepository.findRecentByRoom(roomId, pageable)
			.stream()
			.map(ChatMessageResponse::new)
			.toList();
	}
}
