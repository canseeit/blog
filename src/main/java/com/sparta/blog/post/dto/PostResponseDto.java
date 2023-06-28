package com.sparta.blog.post.dto;

import com.sparta.blog.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponseDto {
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;
    private Long id;
    private String title;
    private String content;
    private String username;

    public PostResponseDto(Post post) {
        this.createAt = post.getCreatedAt();
        this.modifiedAt = post.getModifiedAt();
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.username = post.getUsername();
    }
}