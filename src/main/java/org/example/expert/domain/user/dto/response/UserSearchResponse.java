package org.example.expert.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UserSearchResponse {

    private final Long id;
    private final String email;
    private final String nickname;

    @JsonCreator
    public UserSearchResponse(
            @JsonProperty("id") Long id,
            @JsonProperty("email") String email,
            @JsonProperty("nickname") String nickname) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
    }
}
