package org.example.expert.domain.user.repository;

import org.example.expert.domain.user.dto.response.UserSearchResponse;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findAllByNickname(String nickname);

    // 커버링 인덱스(nickname, email)로 처리되도록 필요한 컬럼만 조회한다.
    @Query("SELECT new org.example.expert.domain.user.dto.response.UserSearchResponse(u.id, u.email, u.nickname) "
        + "FROM User u WHERE u.nickname = :nickname")
    List<UserSearchResponse> searchByNickname(@Param("nickname") String nickname);
}
