package com.sparta.blog.post.service;


import com.sparta.blog.post.dto.PostRequestDto;
import com.sparta.blog.post.dto.PostResponseDto;
import com.sparta.blog.post.entity.Post;
import com.sparta.blog.post.repository.PostRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final HttpServletRequest request;

    public PostService(PostRepository postRepository, HttpServletRequest request) {
        this.postRepository = postRepository;
        this.request = request;
    }

    public PostResponseDto createPost(PostRequestDto requestDto) {
        // RequestDto -> Entity
        Post post = new Post(requestDto);
        // DB 저장
        post.setUsername(request.getUserPrincipal().getName());
        Post savePost = postRepository.save(post);
        // Entity -> ResponseDto
        return new PostResponseDto(savePost);
    }

    public List<PostResponseDto> getAllPosts() {
        // 전체 조회
        return postRepository.findAllByOrderByModifiedAtDesc().stream().map(PostResponseDto::new).toList();
    }

    public PostResponseDto findPost(Long id) {
        Post post = findPostById(id);
        return new PostResponseDto(post);
    }

    @Transactional
    public PostResponseDto updatePost(Long id, PostRequestDto requestDto) {
        Post post = findPostById(id);

        if (!post.getUsername().equals(request.getUserPrincipal().getName())) {
            throw new IllegalArgumentException("본인의 게시글만 수정할 수 있습니다.");
        }
        post.update(requestDto);
        return new PostResponseDto(post);
    }

    @Transactional
    public Map<String, Boolean> deletePost(Long id) {
        Post post = findPostById(id);

        if (!post.getUsername().equals(request.getUserPrincipal().getName())) {
            throw new IllegalArgumentException("본인의 게시글만 삭제할 수 있습니다.");
        }
        // 게시글 삭제
        postRepository.delete(post);

        Map<String, Boolean> response = new HashMap<>();
        response.put("success", true);

        return response;
    }

    private Post findPostById(Long id) {
        // 선택한 게시글 존재 확인
        return postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("선택한 게시글은 존재하지 않습니다."));
    }
}
