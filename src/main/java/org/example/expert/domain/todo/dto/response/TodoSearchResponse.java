package org.example.expert.domain.todo.dto.response;

import lombok.Getter;
import org.example.expert.domain.user.dto.response.UserResponse;

import java.time.LocalDateTime;

@Getter
public class TodoSearchResponse {

    private final Long id;
    private final String title;
    private final String contents;
    private final String weather;
    private final UserResponse user;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
    private final Long managerCount;
    private final Long commentCount;

    public TodoSearchResponse(Long id, String title, String contents, String weather, UserResponse user, LocalDateTime createdAt, LocalDateTime modifiedAt, Long managerCount, Long commentCount) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.weather = weather;
        this.user = user;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.managerCount = managerCount;
        this.commentCount = commentCount;
    }

}
