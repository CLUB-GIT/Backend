package com.club.git.backend_app.service;

import com.club.git.backend_app.dto.AuthDto;
import com.club.git.backend_app.entity.Membre;
import com.club.git.backend_app.entity.RefreshToken;
import com.club.git.backend_app.exception.AuthException;
import com.club.git.backend_app.repository.MembreRepository;
import com.club.git.backend_app.repository.RefreshTokenRepository;
import com.club.git.backend_app.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MembreRepository membreRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Value("${app.jwt.expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshTokenExpirationMs;

    // ── Register ─────────────────────────────────────────────────────────────

    @Transactional
    public AuthDto.AuthResponse register(AuthDto.RegisterRequest request) {

        if (membreRepository.existsByEmail(request.getEmail())) {
            throw new AuthException.EmailAlreadyUsedException(request.getEmail());
        }

        Membre membre = Membre.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail().toLowerCase().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Membre.Role.MEMBRE)
                .statut(Membre.AccountStatus.ACTIF)
                .build();
        if (membre != null) {
            
            membre = membreRepository.save(membre);
            log.info("Nouveau membre enregistré : {} [{}]", membre.getEmail(), membre.getId());
        }

        return buildAuthResponse(membre);
    }

    // ── Login ────────────────────────────────────────────────────────────────

    @Transactional
    public AuthDto.AuthResponse login(AuthDto.LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));
        } catch (AuthenticationException e) {
            throw new AuthException.InvalidCredentialsException();
        }

        Membre membre = membreRepository.findByEmail(request.getEmail())
                .orElseThrow(AuthException.InvalidCredentialsException::new);

        // Vérification du statut (bloqué / suspendu)
        if (membre.getStatut() == Membre.AccountStatus.BLOQUE) {
            throw new AuthException.AccountBlockedException();
        }
        if (membre.getStatut() == Membre.AccountStatus.SUSPENDU) {
            throw new AuthException.AccountSuspendedException();
        }

        // Mise à jour de la dernière connexion
        membre.setDerniereConnexion(LocalDateTime.now());
        membreRepository.save(membre);

        // Révocation des anciens refresh tokens
        refreshTokenRepository.revokeAllByMembre(membre);

        log.info("Connexion réussie : {} [{}]", membre.getEmail(), membre.getRole());
        return buildAuthResponse(membre);
    }

    // ── Refresh token ────────────────────────────────────────────────────────

    @Transactional
    public AuthDto.AuthResponse refresh(AuthDto.RefreshRequest request) {
        RefreshToken rt = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new AuthException.InvalidTokenException("Refresh token introuvable"));

        if (!rt.isValid()) {
            throw new AuthException.InvalidTokenException("Refresh token expiré ou révoqué");
        }

        // Rotation du refresh token (révoque l'ancien, crée un nouveau)
        rt.setRevoked(true);
        refreshTokenRepository.save(rt);

        Membre membre = rt.getMembre();
        return buildAuthResponse(membre);
    }

    // ── Logout ───────────────────────────────────────────────────────────────

    @Transactional
    public void logout(Membre membre) {
        refreshTokenRepository.revokeAllByMembre(membre);
        log.info("Déconnexion : {}", membre.getEmail());
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private AuthDto.AuthResponse buildAuthResponse(Membre membre) {
        String accessToken = jwtService.generateAccessToken(membre);
        String refreshTokenValue = createRefreshToken(membre);

        return AuthDto.AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpirationMs / 1000)
                .membre(AuthDto.AuthResponse.MembreInfo.builder()
                        .id(membre.getId())
                        .nom(membre.getNom())
                        .prenom(membre.getPrenom())
                        .email(membre.getEmail())
                        .role(membre.getRole())
                        .statut(membre.getStatut())
                        .build())
                .build();
    }

    private String createRefreshToken(Membre membre) {
        String tokenValue = UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.builder()
                .membre(membre)
                .token(tokenValue)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpirationMs / 1000))
                .build();

        refreshTokenRepository.save(refreshToken);
        return tokenValue;
    }
}