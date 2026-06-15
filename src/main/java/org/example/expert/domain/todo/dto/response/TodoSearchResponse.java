package org.example.expert.domain.todo.dto.response;

import lombok.Getter;

@Getter
public class TodoSearchResponse {

    private final Long id;
    private final String title;
    private final Long managerCount;
    private final Long commentCount;

    public TodoSearchResponse(Long id, String title, Long managerCount, Long commentCount) {
        this.id = id;
        this.title = title;
        this.managerCount = managerCount;
        this.commentCount = commentCount;
    }

}