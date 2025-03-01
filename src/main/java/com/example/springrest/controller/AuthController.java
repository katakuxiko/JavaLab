package com.example.springrest.controller;

import com.example.springrest.dto.LoginRequest;
import com.example.springrest.dto.RegisterRequest;
import com.example.springrest.entity.Role;
import com.example.springrest.service.JwtService;
import com.example.springrest.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // ✅ Один конструктор для всех зависимостей
    public AuthController(UserService userService, JwtService jwtService,  AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;

    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        String token = jwtService.generateToken(loginRequest.getUsername());
        return ResponseEntity.ok(token);
    }


    @GetMapping("/validate")
    public boolean validateToken(@RequestParam String token, @RequestParam String username) {
        return jwtService.isTokenValid(token, username);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        userService.registerUser(request.getUsername(), request.getPassword(), Role.USER);
        return ResponseEntity.ok("Пользователь зарегистрирован");
    }

}
