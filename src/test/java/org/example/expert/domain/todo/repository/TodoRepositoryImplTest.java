// 검색 Projection Repository 동작을 검증하는 테스트
package org.example.expert.domain.todo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.example.expert.config.PersistenceConfig;
import org.example.expert.config.QuerydslConfig;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
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
class TodoRepositoryImplTest {

	@Autowired
	private TodoRepository todoRepository;

	@Autowired
	private EntityManager entityManager;

	@Test
	void searchTodosWithProjection_returnsSelectedFieldsAndCounts() {
		User writer = new User("writer@test.com", "password", UserRole.USER, "writer");
		User manager = new User("manager@test.com", "password", UserRole.USER, "manager");
		entityManager.persist(writer);
		entityManager.persist(manager);

		Todo todo = new Todo("title", "contents", "Sunny", writer);
		entityManager.persist(todo);
		entityManager.persist(new Manager(manager, todo));
		entityManager.persist(new Comment("comment1", writer, todo));
		entityManager.persist(new Comment("comment2", manager, todo));
		entityManager.flush();
		entityManager.clear();

		Page<TodoSearchResponse> result = todoRepository.searchTodosWithProjection(
			"titl",
			"manager",
			LocalDateTime.now().minusDays(1),
			LocalDateTime.now().plusDays(1),
			PageRequest.of(0, 10)
		);

		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent()).hasSize(1);

		TodoSearchResponse response = result.getContent().get(0);
		assertThat(response.getId()).isEqualTo(todo.getId());
		assertThat(response.getTitle()).isEqualTo("title");
		assertThat(response.getManagerCount()).isEqualTo(2);
		assertThat(response.getCommentCount()).isEqualTo(2);
	}
}
