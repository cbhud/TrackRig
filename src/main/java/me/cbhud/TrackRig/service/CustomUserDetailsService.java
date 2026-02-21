package me.cbhud.TrackRig.service;

import me.cbhud.TrackRig.dto.LoginRequest;
import me.cbhud.TrackRig.dto.RegisterRequest;
import me.cbhud.TrackRig.model.AppUser;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface CustomUserDetailsService extends UserDetailsService {

    AppUser registerUser(RegisterRequest request);

    String login(LoginRequest request);
}