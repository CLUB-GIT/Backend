package com.club.git.backend_app.entity;

import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;

import com.club.git.backend_app.entity.Membre.Role;


@Entity
@Table(name = "utilisateur")

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUtilisateur;

    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Integer soldePoints;

    private Boolean actif;

    private LocalDateTime dateCreation;

    @ManyToOne
    @JoinColumn(name = "commission_id")
    private Commission commission;

}
