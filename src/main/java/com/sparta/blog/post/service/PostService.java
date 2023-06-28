package com.sparta.blog.post.service;


import com.sparta.blog.dto.ApiResult;
import com.sparta.blog.jwt.JwtUtil;
import com.sparta.blog.post.dto.PostRequestDto;
import com.sparta.blog.post.dto.PostResponseDto;
import com.sparta.blog.post.entity.Post;
import com.sparta.blog.post.repository.PostRepository;
import com.sparta.blog.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final HttpServletRequest request;
    private final JwtUtil jwtUtil;

    @Transactional
    public PostResponseDto createPost(PostRequestDto requestDto) {
        // 토큰 체크
        User userEntity = jwtUtil.checkToken(request);

        Post postEntity = Post.builder()
                .requestDto(requestDto)
                .user(userEntity)
                .build();

        postRepository.save(postEntity);
        return new PostResponseDto(postEntity);
    }
    @Transactional(readOnly = true)
    public List<PostResponseDto> getAllPosts() {
        // 전체 조회
        return postRepository.findAllByOrderByModifiedAtDesc().stream().map(PostResponseDto::new).toList();
    }

    @Transactional(readOnly = true)
    public PostResponseDto findPost(Long id) {
        Post post = findPostById(id);
        return new PostResponseDto(post);
    }

    @Transactional
    public PostResponseDto updatePost(Long id, PostRequestDto requestDto) {
        Post post = findPostById(id);
        // 토큰 체크
        User userEntity = jwtUtil.checkToken(request);

        if (!post.getUser().equals(userEntity)) {
            throw new ApiResult("작성자만 수정할 수 있습니다.", HttpStatus.BAD_REQUEST.value());
        }

        post.update(requestDto);
        return new PostResponseDto(post);
    }

    @Transactional
    public ApiResult deletePost(Long id) {
        Post post = findPostById(id);
        // 토큰 체크
        User userEntity = jwtUtil.checkToken(request);
        if (!post.getUser().equals(userEntity)) {
            throw new ApiResult("작성자만 삭제할 수 있습니다.", HttpStatus.BAD_REQUEST.value());
        }

        // 게시글 삭제
        postRepository.delete(post);

        return new ApiResult("게시글 삭제 성공", HttpStatus.OK.value());
    }

    private Post findPostById(Long id) {
        // 선택한 게시글 존재 확인
        return postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("선택한 게시글은 존재하지 않습니다."));
    }
}
