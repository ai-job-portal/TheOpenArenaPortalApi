// src/main/java/com/openarena/openarenaportalapi/controller/UserController.java
package com.openarena.openarenaportalapi.controller;

import com.openarena.openarenaportalapi.dto.UserResponse;
import com.openarena.openarenaportalapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe() {
        UserResponse userResponse = userService.getMe();
        return ResponseEntity.ok(userResponse);
    }
}