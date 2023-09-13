package com.example.gestionemployes.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table
@Entity
public class Departement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Le name est requis")
    @Column(unique = true)
    private String name;
    @NotNull(message = "La description est requis")
    private String description;
    @NotNull(message = "La description est requis")
    private String color;
    @OneToMany(mappedBy = "departement")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Collection<Employee> employees;
    @JsonFormat(pattern = "dd/MM/YYYY")
    private Date createdAt;
    @JsonFormat(pattern = "dd/MM/YYYY")
    private Date updatedAt;
    @Column(name = "supprimer")
    @JsonIgnore
    private boolean supprimer;
}
