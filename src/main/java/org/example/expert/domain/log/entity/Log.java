package org.example.expert.domain.log.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Log {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long requesterUserId;

	@Column(nullable = false)
	private Long todoId;

	@Column(nullable = false)
	private Long managerUserId;

	@Column(nullable = false)
	private String message;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	public Log(Long requesterUserId, Long todoId, Long managerUserId, String message) {
		this.requesterUserId = requesterUserId;
		this.todoId = todoId;
		this.managerUserId = managerUserId;
		this.message = message;
		this.createdAt = LocalDateTime.now();
	}
}
