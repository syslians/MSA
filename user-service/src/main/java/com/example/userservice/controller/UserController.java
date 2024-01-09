package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.Greeting;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.ResponseUser;
import io.micrometer.core.annotation.Timed;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

// RestController: 이 클래스가 REST API 컨트롤러임을 나타냄
@RestController
@RequestMapping("/")
public class UserController {
    private final Environment env;  // 환경 설정 정보에 접근하기 위한 객체
    private final UserService userService; // 사용자 서비스 객체

    @Autowired
    private Greeting greeting;

    @Autowired
    public UserController(Environment env, UserService userService) {
        this.userService = userService;
        this.env = env;
    }

    @GetMapping("/health_check")
    @Timed(value = "users.status", longTask = true)
    public String status() {
        return String.format("It's working in User Service on PORT %s",
                env.getProperty("local.server.port"));
    }

    @GetMapping("/welcome")
    @Timed(value = "users.welcome", longTask = true)
    public String welcome() {
//        return env.getProperty("greeting.message");
        return greeting.getMessage();
    }



    // POST 요청 /users 회원가입 처리
    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser user) { // JSON 본문에서 사용자 정보 추출
        ModelMapper mapper = new ModelMapper(); // ModelMapper를 사용하여 RequestUser를 UserDto로 변환
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(user, UserDto.class);

        // 사용자 서비스에게 사용자 생성 요청
        userService.createUser(userDto);

        /* userDto -> ResponseUser 변환. */
        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);

        // 성공 메시지 반환(상태코드 201번 및 responseUser 객체(email, name, userId))
        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }



    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getuser(@PathVariable("userId") String userId) {
        UserDto userDto = userService.getUserById(userId);

        ResponseUser returnValue = new ModelMapper().map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.OK).body(returnValue);
    }
}
