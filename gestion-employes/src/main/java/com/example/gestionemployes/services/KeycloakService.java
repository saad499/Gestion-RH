package com.example.gestionemployes.services;

import com.example.gestionemployes.entities.Employee;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@Slf4j
public class KeycloakService{
    private final WebClient webClient;

    public KeycloakService(@Value("${keycloak.auth-server-url}") String authServerUrl, WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(authServerUrl).build();
    }

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;
    @Value("${keycloak.client.id}")
    private String clientId;
    @Value("${keycloak.client.name}")
    private String clientName;
    @Value("${keycloak.realm}")
    private String realm;
    private final Map<String, String> realmRoles = Map.of(
            "client-employee", "employee",
            "client-manager", "manager",
            "client-admin", "admin",
            "client-hr-personal", "hrpersonal");

    private MultiValueMap<String, String> loginData() {
        MultiValueMap<String, String> loginData = new LinkedMultiValueMap<>();
        loginData.add("client_id", clientName);
        loginData.add("client_secret", clientSecret);
        loginData.add("grant_type", "client_credentials");
        return loginData;
    }

    private HttpHeaders authHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }

    public String getKeycloakAccessToken() {
        String uri = "/realms/" + realm + "/protocol/openid-connect/token";
        return webClient.post()
                .uri(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData(loginData()))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    // Here we get the status and the body of the response when it's an error
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody -> {
                                System.out.println("Status: " + clientResponse.statusCode());
                                System.out.println("Response Body: " + errorBody);
                                return Mono.error(new RuntimeException("Error: " + errorBody));
                            });
                })
                .bodyToMono(Map.class)
                .map(body -> (String) body.get("access_token"))
                .block();
    }

    public void createUserInKeycloak(String token, Employee employee) {
        verifyClientRole(employee);
        String username = employee.getUsername();

        // Check if the user already exists
        Optional<String> existingUserId = getUserIdByUsername(token, username);
        if (existingUserId.isPresent()) {
            throw new IllegalStateException("User already exists");
        }

        // Creating the user
        ResponseEntity<Void> block = webClient.post()
                .uri("/admin/realms/" + realm + "/users")
                .headers(httpHeaders -> httpHeaders.addAll(authHeaders(token)))
                .body(BodyInserters.fromValue(createUserRepresentation(employee)))
                .retrieve()
                .toBodilessEntity()
                .block();

        log.info("Block ---------------------- {} ", block);
        String userId = existingUserId.orElseGet(() -> getUserIdByUsername(token, username)
                .orElseThrow(() -> new IllegalStateException("User was not found")));

        RoleRepresentation realmRoleEntity = fetchRoleByName(token, "/admin/realms/" + realm + "/roles/" + realmRoles.get(employee.getRole()));
        RoleRepresentation clientRoleEntity = fetchRoleByName(token, "/admin/realms/" + realm + "/clients/" + clientId + "/roles/" + employee.getRole());

        assignRealmRoleToUser(token, userId, realmRoleEntity);
        assignClientRoleToUser(token, userId, clientRoleEntity);
    }

    public void deleteUserFromKeycloak(String token, String username) {
        // Get the user ID by username
        Optional<String> userIdOptional = getUserIdByUsername(token, username);
        if (userIdOptional.isPresent()) {
            String userId = userIdOptional.get();
            // Delete the user from Keycloak
            webClient.delete()
                    .uri("/admin/realms/" + realm + "/users/" + userId)
                    .headers(httpHeaders -> httpHeaders.addAll(authHeaders(token)))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            log.info("User {} has been deleted from Keycloak.", username);
        } else {
            throw new IllegalArgumentException("User not found in Keycloak: " + username);
        }
    }

    public void resetUserPasswordInKeycloak(String token, String username, String newPassword) {
        // Get the user ID by username
        Optional<String> userIdOptional = getUserIdByUsername(token, username);
        if (userIdOptional.isPresent()) {
            String userId = userIdOptional.get();
            // Update the user's password in Keycloak
            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(newPassword);
            passwordCred.setTemporary(false);

            webClient.put()
                    .uri("/admin/realms/" + realm + "/users/" + userId + "/reset-password")
                    .headers(httpHeaders -> httpHeaders.addAll(authHeaders(token)))
                    .body(BodyInserters.fromValue(passwordCred))
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            log.info("Password for user {} has been reset in Keycloak.", username);
        } else {
            throw new IllegalArgumentException("User not found in Keycloak: " + username);
        }
    }


    public void verifyClientRole(Employee employee) {
        String clientRole = employee.getRole();
        if (!realmRoles.containsKey(clientRole)) {
            throw new IllegalArgumentException("Invalid client role: " + clientRole);
        }
    }

    private UserRepresentation createUserRepresentation(Employee employee) {
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
        log.info("User ----------------- {}", user.toString());
        return user;
    }

    private RoleRepresentation fetchRoleByName(String token, String uriPath) {
        return webClient.get()
                .uri(uriPath)
                .headers(httpHeaders -> httpHeaders.addAll(authHeaders(token)))
                .retrieve()
                .bodyToMono(RoleRepresentation.class)
                .block();
    }

    private void assignRoleToUser(String token, String userId, RoleRepresentation roleEntity, String roleMappingPath) {
        webClient.post()
                .uri("/admin/realms/" + realm + "/users/" + userId + roleMappingPath)
                .headers(httpHeaders -> httpHeaders.addAll(authHeaders(token)))
                .body(BodyInserters.fromValue(Collections.singletonList(roleEntity)))
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    private void assignRealmRoleToUser(String token, String userId, RoleRepresentation realmRoleEntity) {
        assignRoleToUser(token, userId, realmRoleEntity, "/role-mappings/realm");
    }

    private void assignClientRoleToUser(String token, String userId, RoleRepresentation clientRoleEntity) {
        assignRoleToUser(token, userId, clientRoleEntity, "/role-mappings/clients/" + clientId);
    }

    private Optional<String> getUserIdByUsername(String token, String username) {
        List<UserRepresentation> usersResponse = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/admin/realms/" + realm + "/users")
                        .queryParam("username", username)
                        .build())
                .headers(httpHeaders -> httpHeaders.addAll(authHeaders(token)))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UserRepresentation>>() {
                })
                .block();

        if (usersResponse != null && !usersResponse.isEmpty()) {
            UserRepresentation userRepresentation = usersResponse.get(0);
            return Optional.ofNullable(userRepresentation.getId());
        }

        return Optional.empty();
    }
}