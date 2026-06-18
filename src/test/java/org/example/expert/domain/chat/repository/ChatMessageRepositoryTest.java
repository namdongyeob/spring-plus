// 익명(sender=null) 메시지도 방 히스토리 조회에 포함되는지 검증
package org.example.expert.domain.chat.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.example.expert.config.PersistenceConfig;
import org.example.expert.config.QuerydslConfig;
import org.example.expert.domain.chat.entity.ChatMessage;
import org.example.expert.domain.chat.entity.ChatRoom;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

import jakarta.persistence.EntityManager;

@DataJpaTest
@Import({PersistenceConfig.class, QuerydslConfig.class})
@TestPropertySource(properties = {
	"spring.jpa.hibernate.ddl-auto=create-drop",
	"spring.sql.init.mode=never",
	"jwt.secret.key=test-secret-key-for-repository-test"
})
class ChatMessageRepositoryTest {

	@Autowired
	private ChatMessageRepository chatMessageRepository;

	@Autowired
	private EntityManager entityManager;

	@Test
	void findRecentByRoom_includesAnonymousMessages() {
		User sender = new User("user@test.com", "password", UserRole.USER, "유저닉");
		entityManager.persist(sender);

		ChatRoom room = new ChatRoom("room");
		entityManager.persist(room);

		entityManager.persist(new ChatMessage(room, sender, "유저닉", "안녕하세요"));
		entityManager.persist(new ChatMessage(room, null, "익명-abcd1234", "익명 메시지"));
		entityManager.flush();
		entityManager.clear();

		List<ChatMessage> messages = chatMessageRepository.findRecentByRoom(room.getId(), PageRequest.of(0, 50));

		// left join fetch이므로 sender가 null인 익명 메시지도 함께 반환되어야 한다.
		assertThat(messages).hasSize(2);
		assertThat(messages).extracting(ChatMessage::getSenderName)
			.containsExactlyInAnyOrder("유저닉", "익명-abcd1234");
	}
}
