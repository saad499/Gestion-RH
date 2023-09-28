package com.example.gestionemployes.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data @Entity
@Table(name="employee", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"cin"}),
        @UniqueConstraint(columnNames = {"email"}),
        @UniqueConstraint(columnNames = {"telephone"}),
        @UniqueConstraint(columnNames = {"username"}),
})
public class Employee {
    @Id
    @NotBlank(message = "Le username est requis.")
    private String username;
    @NotBlank(message = "Le nom est requis.")
    private String nom;
    @NotBlank(message = "Le prenom est requis.")
    private String prenom;
    @Column(unique = true)
    @NotBlank(message = "Le cin est requis.")
    private String cin;
    @NotNull(message = "La date de naissance est requis.")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date dateNaissance;
    @NotNull(message = "L'adresse est requis")
    private String adresse;
    @Column(unique = true)
    @Email(message = "L'email n'est pas valide")
    @NotBlank(message = "L'email est requis.")
    private String email;
    @Column(unique = true)
    @NotBlank(message = "Le numéro de téléphone est requis.")
    private String telephone;
    @NotNull(message = "Le poste est requis")
    private String poste;
    @NotNull(message = "Le département est requis")
    @ManyToOne
    private Departement departement;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date dateEmbauche;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date dateSortie;
    @NotNull(message = "Le salaire est requis")
    private Double salaire;
    //@NotNull(message = "Le mot de passe est requis")
    @JsonIgnore
    private String motDePasse;


    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Employee manager;
    @JsonFormat(pattern = "dd/MM/YYYY")
    private Date createdAt;
    @JsonFormat(pattern = "dd/MM/YYYY")
    private Date updatedAt;
    @Column(name = "supprimer")
    @JsonIgnore
    private boolean supprimer;
    private String clientRole;
}
