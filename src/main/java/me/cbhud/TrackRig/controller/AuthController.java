package me.cbhud.TrackRig.controller;

import jakarta.validation.Valid;
import me.cbhud.TrackRig.dto.LoginRequest;
import me.cbhud.TrackRig.dto.RegisterRequest;
import me.cbhud.TrackRig.dto.UserResponse;
import me.cbhud.TrackRig.model.AppUser;
import me.cbhud.TrackRig.service.CustomUserDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CustomUserDetailsService userDetailsService;

    public AuthController(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        String token = userDetailsService.login(request);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid RegisterRequest request) {
        AppUser user = userDetailsService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(user));
    }
}