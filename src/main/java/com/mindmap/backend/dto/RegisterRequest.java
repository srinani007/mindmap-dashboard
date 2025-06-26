package com.mindmap.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterRequest {
    // Getters and setters
    private String username;
    private String email;
    private String password;

}
