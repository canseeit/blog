package com.sparta.blog.user.controller;

import com.sparta.blog.user.dto.ApiResult;
import com.sparta.blog.user.dto.LoginRequestDto;
import com.sparta.blog.user.dto.SignupRequestDto;
import com.sparta.blog.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ApiResult signup(@RequestBody @Valid SignupRequestDto requestDto) {
        return userService.signup(requestDto);
    }

    @PostMapping("/login")
    public ApiResult login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        userService.login(loginRequestDto, response);
        return new ApiResult("로그인 성공", HttpStatus.OK.value());
    }
}