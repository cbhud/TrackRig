package me.cbhud.TrackRig.dto;

import lombok.Data;
import me.cbhud.TrackRig.model.AppUser;

import java.time.LocalDateTime;

@Data
public class UserResponse {

    // FIX: Changed Long to Integer — must match AppUser.id type (Integer)
    // since SQL 'SERIAL' maps to INTEGER.
    private Integer id;
    private String email;
    private String fullName;
    private String role;
    private LocalDateTime createdAt;

    public static UserResponse from(AppUser user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setRole(user.getRole().name());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}
