package com.healthdesk.dto;

import com.healthdesk.model.Role;
import lombok.*;

@Data @AllArgsConstructor
public class AuthResponse {
    private String token;
    private String name;
    private String email;
    private Role role;
}
