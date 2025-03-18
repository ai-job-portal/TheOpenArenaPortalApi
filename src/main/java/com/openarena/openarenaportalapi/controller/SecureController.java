package com.openarena.openarenaportalapi.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/securemyself")
public class SecureController {

    @GetMapping("/v1/secure/csrf")
    public CsrfToken secureCsrf(CsrfToken token) {
        return token;
    }
}
