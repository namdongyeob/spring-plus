package org.example.expert.domain.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.example.expert.domain.chat.dto.request.ChatMessageSendRequest;
import org.example.expert.domain.chat.dto.response.ChatMessageResponse;
import org.example.expert.domain.chat.entity.ChatMessage;
import org.example.expert.domain.chat.entity.ChatRoom;
import org.example.expert.domain.chat.repository.ChatMessageRepository;
import org.example.expert.domain.chat.repository.ChatRoomRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {

	@Mock
	private ChatMessageRepository chatMessageRepository;

	@Mock
	private ChatRoomRepository chatRoomRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private ChatMessageService chatMessageService;

	@Test
	void saveMessage_savesMessageWithAuthenticatedUserAndRoom() {
		Long userId = 1L;
		Long roomId = 10L;
		User sender = new User("user@test.com", "password", UserRole.USER, "nickname");
		ChatRoom room = new ChatRoom("test room");
		ChatMessageSendRequest request = new ChatMessageSendRequest(roomId, "hello");

		when(userRepository.findById(userId)).thenReturn(Optional.of(sender));
		when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(room));
		when(chatMessageRepository.save(any(ChatMessage.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		ChatMessageResponse response = chatMessageService.saveMessage(userId, "nickname", request);

		ArgumentCaptor<ChatMessage> captor = ArgumentCaptor.forClass(ChatMessage.class);
		verify(chatMessageRepository).save(captor.capture());

		ChatMessage savedMessage = captor.getValue();
		assertThat(savedMessage.getSender()).isEqualTo(sender);
		assertThat(savedMessage.getSenderName()).isEqualTo("nickname");
		assertThat(savedMessage.getChatRoom()).isEqualTo(room);
		assertThat(savedMessage.getContent()).isEqualTo("hello");
		assertThat(response.getContent()).isEqualTo("hello");
		assertThat(response.getSenderNickname()).isEqualTo("nickname");
	}

	@Test
	void saveMessage_savesAnonymousMessageWithoutUserLookup() {
		Long roomId = 10L;
		ChatRoom room = new ChatRoom("test room");
		ChatMessageSendRequest request = new ChatMessageSendRequest(roomId, "hi");

		when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(room));
		when(chatMessageRepository.save(any(ChatMessage.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		ChatMessageResponse response = chatMessageService.saveMessage(null, "익명-abcd1234", request);

		ArgumentCaptor<ChatMessage> captor = ArgumentCaptor.forClass(ChatMessage.class);
		verify(chatMessageRepository).save(captor.capture());

		ChatMessage savedMessage = captor.getValue();
		assertThat(savedMessage.getSender()).isNull();
		assertThat(savedMessage.getSenderName()).isEqualTo("익명-abcd1234");
		assertThat(response.getSenderId()).isNull();
		assertThat(response.getSenderNickname()).isEqualTo("익명-abcd1234");
		verifyNoInteractions(userRepository);
	}
}
