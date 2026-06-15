package org.example.expert.domain.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageSendRequest {

	private Long roomId;
	private String content;
}
