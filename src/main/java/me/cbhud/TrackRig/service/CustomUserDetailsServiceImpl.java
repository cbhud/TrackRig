package me.cbhud.TrackRig.service;

import me.cbhud.TrackRig.dto.LoginRequest;
import me.cbhud.TrackRig.repository.AppUserRepository;
import me.cbhud.TrackRig.security.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import me.cbhud.TrackRig.dto.RegisterRequest;
import me.cbhud.TrackRig.model.AppUser;
import me.cbhud.TrackRig.model.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public CustomUserDetailsServiceImpl(AppUserRepository userRepository, PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public String login(LoginRequest request) {
        AppUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        return jwtUtil.generateToken(user.getEmail(), user.getRole().name());
    }

    @Override
    @Transactional
    public AppUser registerUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already taken");
        }

        AppUser newUser = new AppUser();
        newUser.setFullName(request.getFullName());
        newUser.setEmail(request.getEmail());

        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        newUser.setRole(Role.EMPLOYEE);

        return userRepository.save(newUser);
    }
}
