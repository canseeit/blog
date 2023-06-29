package com.sparta.blog.user.service;

import com.sparta.blog.dto.ApiResult;
import com.sparta.blog.jwt.JwtUtil;
import com.sparta.blog.user.dto.LoginRequestDto;
import com.sparta.blog.user.dto.SignupRequestDto;
import com.sparta.blog.user.entity.User;
import com.sparta.blog.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;


    @Transactional
    public ApiResult signup(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();
        String role = requestDto.getRole();

        // 회원 중복 확인
        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            throw new ApiResult("중복된 username 입니다.", HttpStatus.BAD_REQUEST.value());
        }

        // 사용자 등록
        User user = new User(username, password, role);
        userRepository.save(user);

        return new ApiResult("회원가입 성공", HttpStatus.OK.value());
    }

    @Transactional(readOnly = true)
    public void login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        // 사용자 확인
        User userEntity = userRepository.findByUsername(username).orElseThrow(
                () -> new ApiResult("회원을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST.value())
        );

        // 비밀번호 확인
        if (!userEntity.getPassword().equals(password)) {
            throw new IllegalArgumentException();
        }
        // JWT Token 생성 및 반환
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(userEntity.getUsername()));
    }
}