package com.club.git.backend_app.controller;

import com.club.git.backend_app.dto.AuthDto;
import com.club.git.backend_app.entity.Membre;
import com.club.git.backend_app.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Register, login, refresh token, logout")
public class AuthController {

    private final AuthService authService;

    // POST /api/v1/auth/register
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Inscription d'un nouveau membre")
    public ResponseEntity<AuthDto.AuthResponse> register(
            @Valid @RequestBody AuthDto.RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    // POST /api/v1/auth/login
    @PostMapping("/login")
    @Operation(summary = "Connexion — retourne access + refresh token")
    public ResponseEntity<AuthDto.AuthResponse> login(
            @Valid @RequestBody AuthDto.LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // POST /api/v1/auth/refresh
    @PostMapping("/refresh")
    @Operation(summary = "Renouvelle l'access token via le refresh token")
    public ResponseEntity<AuthDto.AuthResponse> refresh(
            @Valid @RequestBody AuthDto.RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    // POST /api/v1/auth/logout
    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Révoque tous les refresh tokens du membre connecté")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal Membre membre) {
        authService.logout(membre);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/auth/me
    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Retourne les informations du membre connecté")
    public ResponseEntity<AuthDto.AuthResponse.MembreInfo> me(
            @AuthenticationPrincipal Membre membre) {
        return ResponseEntity.ok(AuthDto.AuthResponse.MembreInfo.builder()
                .id(membre.getId())
                .nom(membre.getNom())
                .prenom(membre.getPrenom())
                .email(membre.getEmail())
                .role(membre.getRole())
                .statut(membre.getStatut())
                .build());
    }
}