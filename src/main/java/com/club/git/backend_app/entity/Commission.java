package com.club.git.backend_app.entity;

import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Commission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCommission;

    private String nom;
    private String description;

    private LocalDate dateCreation;

    private Boolean actif;

}
