package com.mindmap.backend.dto;


import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class AuthRequest {
    // Getters and setters
    private String email;
    private String password;

}
