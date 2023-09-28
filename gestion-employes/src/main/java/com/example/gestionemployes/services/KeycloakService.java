package com.example.gestionemployes.services;


import com.example.gestionemployes.entities.Employee;

public interface KeycloakService {

    String getKeycloakAccessToken();

    void createUserInKeycloak(String token, Employee employee);
    void deleteUserFromKeycloak(String token, String username);
     void updateUserInKeycloak(String token, Employee employee) ;
    void resetUserPasswordInKeycloak(String token, String username, String newPassword);

    void setUserEnabledStatus(String token, String username, boolean enabled);

    void verifyClientRole(Employee employee);

}
