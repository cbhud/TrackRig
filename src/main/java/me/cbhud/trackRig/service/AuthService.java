package me.cbhud.trackRig.service;

import me.cbhud.trackRig.dto.AuthResponse;
import me.cbhud.trackRig.dto.LoginRequest;
import me.cbhud.trackRig.dto.RegisterRequest;
import me.cbhud.trackRig.dto.RegisterResponse;

public interface AuthService {
    public RegisterResponse register(RegisterRequest registerRequest);
    public AuthResponse login(LoginRequest loginRequest);
}
