package org.example.expert.domain.log.service;

import org.example.expert.domain.log.entity.Log;
import org.example.expert.domain.log.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogService {

	private final LogRepository logRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveManagerLog(Long requesterUserId, Long todoId, Long managerUserId) {
		String message = "Manager save request";

		Log log = new Log(
			requesterUserId,
			todoId,
			managerUserId,
			message
		);

		logRepository.save(log);
	}
}

