package dev.likelion.summer.controller;

import dev.likelion.summer.dto.UserDto;
import dev.likelion.summer.entity.User;
import dev.likelion.summer.response.UserResponse;
import dev.likelion.summer.resquest.UserUpdateRequest;
import dev.likelion.summer.service.KakaoService;
import dev.likelion.summer.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final KakaoService kakaoService;

    @GetMapping("/add/kakao/login")
    public ModelAndView loginPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("kakaoLogin");

        return modelAndView;
    }

    @GetMapping("/add/kakao/permission")
    public void kakaoLogin(@RequestParam(value = "code", required = false) String code) throws IOException {
        String[] tokens = kakaoService.getAccessToken(code);
        String accessToken = tokens[0];
        String refreshToken = tokens[1];

        HashMap<String, Object> userInfo = kakaoService.getUserInfo(accessToken);
        // access token 받은 이후 사용자 정보 받아오는 것을 어디에 처리할 것인지?

        userAdd(accessToken, refreshToken, userInfo.get("email").toString(), userInfo.get("nickname").toString());

    }

    @GetMapping("/login/{accessToken}")
    public ResponseEntity<UserResponse> userLogin(@PathVariable String accessToken) {
        User getUser = userService.getUserByToken(accessToken);

        return ResponseEntity.ok(UserResponse.toUserResponse(getUser));
    }

    @PostMapping("/add")
    public ResponseEntity<Long> userAdd(String accessToken, String refreshToken, String email, String nickname) {
        Long userId = userService.addUser(UserDto.toUserDto(accessToken, refreshToken, email, nickname));

        return ResponseEntity.ok(userId);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);

        return ResponseEntity.ok(null);
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<Long> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest userUpdateRequest) {
        Long userId = userService.updateUserId(id, UserDto.toUserDto(userUpdateRequest));

        return ResponseEntity.ok(userId);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        User getUser = userService.getUserById(id);

        return ResponseEntity.ok(UserResponse.toUserResponse(getUser));
    }
}
