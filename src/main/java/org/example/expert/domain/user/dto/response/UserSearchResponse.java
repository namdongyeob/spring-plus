package org.example.expert.domain.user.dto.response;

import lombok.Getter;
import org.example.expert.domain.user.entity.User;

@Getter
public class UserSearchResponse {

    private final Long id;
    private final String email;
    private final String nickname;

    public UserSearchResponse(Long id, String email, String nickname) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
    }

    public static UserSearchResponse from(User user) {
        return new UserSearchResponse(user.getId(), user.getEmail(), user.getNickname());
    }
}
