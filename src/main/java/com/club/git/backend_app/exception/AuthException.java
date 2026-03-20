package com.club.git.backend_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class AuthException {

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class EmailAlreadyUsedException extends RuntimeException {
        public EmailAlreadyUsedException(String email) {
            super("L'email est déjà utilisé : " + email);
        }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException() {
            super("Email ou mot de passe incorrect");
        }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class AccountBlockedException extends RuntimeException {
        public AccountBlockedException() {
            super("Compte bloqué : solde de points critique. Contactez le Bureau.");
        }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class AccountSuspendedException extends RuntimeException {
        public AccountSuspendedException() {
            super("Compte suspendu temporairement. Contactez votre Chef de Commission.");
        }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class InvalidTokenException extends RuntimeException {
        public InvalidTokenException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class AccessDeniedException extends RuntimeException {
        public AccessDeniedException() {
            super("Vous n'avez pas les permissions nécessaires pour cette action");
        }
    }
}