package com.mindmap.backend.dto;

/**
 * Immutable DTO for carrying back a logged‐in user’s info + JWT.
 */
public record LoginResponse(
        Long id,
        String email,
        String role,
        String token
) { }
