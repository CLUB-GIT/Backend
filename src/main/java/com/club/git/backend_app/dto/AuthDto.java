package com.club.git.backend_app.dto;

import com.club.git.backend_app.entity.Membre;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthDto {

    // ── Requête Register ─────────────────────────────────────────────────────

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {

        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 100)
        private String nom;

        @NotBlank(message = "Le prénom est obligatoire")
        @Size(max = 100)
        private String prenom;

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Format email invalide")
        private String email;

        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
        private String password;
    }

    // ── Requête Login ────────────────────────────────────────────────────────

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {

        @NotBlank(message = "L'email est obligatoire")
        @Email
        private String email;

        @NotBlank(message = "Le mot de passe est obligatoire")
        private String password;
    }

    // ── Requête Refresh ──────────────────────────────────────────────────────

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshRequest {

        @NotBlank
        private String refreshToken;
    }

    // ── Réponse Auth ─────────────────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthResponse {
        private String accessToken;
        private String refreshToken;
        private String tokenType;
        private long expiresIn;         // secondes
        private MembreInfo membre;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class MembreInfo {
            private Integer id;
            private String nom;
            private String prenom;
            private String email;
            private Membre.Role role;
            private Membre.AccountStatus statut;
        }
    }
}