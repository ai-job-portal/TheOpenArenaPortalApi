// EmployerController.java
package com.openarena.openarenaportalapi.controller;

import com.openarena.openarenaportalapi.dto.DashboardOverviewDTO;
import com.openarena.openarenaportalapi.model.User;
import com.openarena.openarenaportalapi.service.EmployerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import com.openarena.openarenaportalapi.service.UserService;


@RestController
@RequestMapping("/employer")
@PreAuthorize("hasRole('ROLE_EMPLOYER')")
public class EmployerController {

    @Autowired
    private EmployerService employerService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard-overview")
    public ResponseEntity<?> getDashboardOverview() {
        try {
            DashboardOverviewDTO overview = employerService.getDashboardOverview();
            return ResponseEntity.ok(overview);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}