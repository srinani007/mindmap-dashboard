package com.mindmap.backend.service;

import com.mindmap.backend.dto.AuthRequest;
import com.mindmap.backend.dto.LoginResponse;
import com.mindmap.backend.dto.RegisterRequest;
import com.mindmap.backend.model.Role;
import com.mindmap.backend.model.User;
import com.mindmap.backend.repository.UserRepository;
import com.mindmap.backend.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Registers a brand-new user with Role.USER, then returns a LoginResponse containing
     * id, email, role, and a freshly minted JWT.
     */
    public LoginResponse register(RegisterRequest request) {
        // 1) check if email already in use
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        // 2) build & persist
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)                // default to USER
                .build();
        userRepo.save(user);

        // 3) generate token
        String token = jwtUtil.generateToken(
                userDetailsService.loadUserByUsername(user.getEmail())
        );

        // 4) return full LoginResponse
        return new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                token
        );
    }

    /**
     * Authenticates an existing user (by email + password), then returns LoginResponse.
     */
    public LoginResponse login(AuthRequest request) {
        // 1) perform authentication
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2) load UserDetails to feed to JwtUtil
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 3) pull our User entity (to get id + role)
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 4) generate token
        String token = jwtUtil.generateToken(userDetails);

        // 5) return
        return new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                token
        );
    }
}
