package org.example.expert.domain.chat.dto.response;

import java.time.LocalDateTime;

import org.example.expert.domain.chat.entity.ChatMessage;

import lombok.Getter;

@Getter
public class ChatMessageResponse {
	private final Long messageId;
	private final String content;
	private final Long senderId;
	private final String senderNickname;
	private final LocalDateTime createdAt;

	public ChatMessageResponse(ChatMessage chatMessage) {
		this.messageId = chatMessage.getId();
		this.content = chatMessage.getContent();
		this.senderId = chatMessage.getSender() != null ? chatMessage.getSender().getId() : null;
		this.senderNickname = chatMessage.getSenderName();
		this.createdAt = chatMessage.getCreatedAt();
	}
}
