package com.example.gestionemployes.services;

import com.example.gestionemployes.entities.Employee;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class KeycloakService {
    @Autowired
    private Keycloak keycloak;

    @Autowired
    public KeycloakService(){
        keycloak = KeycloakBuilder.builder()
                .serverUrl("http://localhost:8081/auth")
                .realm("Keycloak_RH")
                .clientId("RH-app")
                .build();
    }

    public void registerEmployeeInKeycloak(Employee employee){
        UserRepresentation user = new UserRepresentation();
        user.setUsername(employee.getUsername());
        user.setFirstName(employee.getNom());
        user.setLastName(employee.getPrenom());
        user.setEmail(employee.getEmail());
        user.setEnabled(true);
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(employee.getMotDePasse());
        passwordCred.setTemporary(false);
        user.setCredentials(Collections.singletonList(passwordCred));
        keycloak.realm("keycloak_RH").users().create(user);
    }
}
