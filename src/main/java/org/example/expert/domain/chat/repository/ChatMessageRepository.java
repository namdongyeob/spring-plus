package org.example.expert.domain.chat.repository;

import java.util.List;

import org.example.expert.domain.chat.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

	@Query("""
		select m
		from ChatMessage m
		left join fetch m.sender
		where m.chatRoom.id = :roomId
		order by m.id desc
		""")
	List<ChatMessage> findRecentByRoom(@Param("roomId") Long roomId, Pageable pageable);
}
