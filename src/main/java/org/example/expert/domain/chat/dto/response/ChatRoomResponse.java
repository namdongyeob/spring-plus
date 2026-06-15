package org.example.expert.domain.chat.dto.response;

import java.time.LocalDateTime;

import org.example.expert.domain.chat.entity.ChatRoom;

import lombok.Getter;

@Getter
public class ChatRoomResponse {
	private final Long roomId;
	private final String name;
	private final LocalDateTime createdAt;

	public ChatRoomResponse(ChatRoom chatRoom) {
		this.roomId = chatRoom.getId();
		this.name = chatRoom.getName();
		this.createdAt = chatRoom.getCreatedAt();
	}
}
