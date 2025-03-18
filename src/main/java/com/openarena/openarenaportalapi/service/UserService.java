// src/main/java/com/openarena/openarenaportalapi/service/UserService.java
package com.openarena.openarenaportalapi.service;

import com.openarena.openarenaportalapi.dto.UserResponse;

public interface UserService {
    UserResponse getMe();
}