package org.example.expert.domain.todo.repository;

import static org.example.expert.domain.comment.entity.QComment.*;
import static org.example.expert.domain.manager.entity.QManager.*;
import static org.example.expert.domain.todo.entity.QTodo.*;
import static org.example.expert.domain.user.entity.QUser.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Page<TodoSearchResponse> searchTodosWithProjection(String title, String nickname, LocalDateTime start, LocalDateTime end, Pageable pageable) {
		BooleanBuilder condition = createSearchCondition(title, nickname, start, end);
		NumberExpression<Long> managerCount = manager.id.countDistinct();
		NumberExpression<Long> commentCount = comment.id.countDistinct();

		List<TodoSearchResponse> contents = jpaQueryFactory
			.select(Projections.constructor(
				TodoSearchResponse.class,
				todo.id,
				todo.title,
				managerCount,
				commentCount
			))
			.from(todo)
			.leftJoin(todo.managers, manager)
			.leftJoin(todo.comments, comment)
			.where(condition)
			.groupBy(
				todo.id,
				todo.title,
				todo.createdAt
			)
			.orderBy(todo.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = jpaQueryFactory
			.select(todo.count())
			.from(todo)
			.where(condition)
			.fetchOne();

		return new PageImpl<>(contents, pageable, total == null ? 0 : total);
	}

	@Override
	public Optional<Todo> findByIdWithUser(Long todoId) {

		Todo result = jpaQueryFactory
			.selectFrom(todo)
			.leftJoin(todo.user, user)
			.fetchJoin()
			.where(todo.id.eq(todoId))
			.fetchOne();

		return Optional.ofNullable(result);
	}

	private BooleanBuilder createSearchCondition(String title, String nickname, LocalDateTime start, LocalDateTime end) {
		BooleanBuilder condition = new BooleanBuilder();

		if (StringUtils.hasText(title)) {
			condition.and(todo.title.contains(title));
		}

		if (start != null) {
			condition.and(todo.createdAt.goe(start));
		}

		if (end != null) {
			condition.and(todo.createdAt.loe(end));
		}

		if (StringUtils.hasText(nickname)) {
			QManager subManager = new QManager("subManager");
			QUser subUser = new QUser("subUser");
			condition.and(JPAExpressions
				.selectOne()
				.from(subManager)
				.join(subManager.user, subUser)
				.where(subManager.todo.eq(todo)
					.and(subUser.nickname.contains(nickname)))
				.exists());
		}

		return condition;
	}
}