package org.testing.p2p_transaction.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.testing.p2p_transaction.dto.UserRegistrationDto;
import org.testing.p2p_transaction.dto.UserResponseDto;
import org.testing.p2p_transaction.service.UserService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody UserRegistrationDto request) {
       try {
            UserResponseDto userDto = userService.registerUser(request);
            return ResponseEntity.ok(userDto);
       } catch (IllegalArgumentException e) {
           return ResponseEntity.badRequest().build();
       } catch (Exception e) {
           return ResponseEntity.status(500).build();
       }
    }
}
