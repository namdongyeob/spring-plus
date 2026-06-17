package org.example.expert.domain.chat.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.common.entity.Timestamped;
import org.example.expert.domain.user.entity.User;

@Entity
@Getter
@NoArgsConstructor
public class ChatMessage extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chat_room_id", nullable = false)
	private ChatRoom chatRoom;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_id")
	private User sender; // 익명 메시지일 경우 null

	private String senderName; // 발신 시점의 표시명 스냅샷 (익명 포함, 닉변/탈퇴에도 보존)

	private String content;

	public ChatMessage(ChatRoom chatRoom, User sender, String senderName, String content) {
		this.chatRoom = chatRoom;
		this.sender = sender;
		this.senderName = senderName;
		this.content = content;
	}
}
