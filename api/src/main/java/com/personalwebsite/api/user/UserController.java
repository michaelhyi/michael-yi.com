package com.personalwebsite.api.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/user")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("{email}")
    public ResponseEntity<User> readUserByEmail(
            @PathVariable("email") String email
    ) {
        return ResponseEntity.ok(service.readUserByEmail(email));
    }
}