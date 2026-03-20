package com.club.git.backend_app.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "ClubGIT API", version = "1.0", description = "API backend de la plateforme de gestion du Club GIT - ENSPD Douala", contact = @Contact(name = "KAMDEU CHRETIEN", email = "kamdeuchretien@gmail.com")))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", description = "Token JWT obtenu via /api/v1/auth/login")
public class OpenApiConfig {
}