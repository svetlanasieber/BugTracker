package com.bugtracker.bugtracker.web.api;

import com.bugtracker.bugtracker.config.security.JwtService;
import com.bugtracker.bugtracker.web.api.dto.LoginRequest;
import com.bugtracker.bugtracker.web.api.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ApiAuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        final String jwt = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(new LoginResponse(jwt));
    }
}
