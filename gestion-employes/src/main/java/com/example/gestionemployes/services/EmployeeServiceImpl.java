package com.example.gestionemployes.services;


import com.example.gestionemployes.entities.Employee;
import com.example.gestionemployes.repositories.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final KeycloakService keycloakService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository,  KeycloakService keycloakService, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.keycloakService = keycloakService;
        this.passwordEncoder = passwordEncoder;

    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllEmployees(int pageNumber, int pageSize, Boolean state, String[] sortFields, String[] sortOrders, String username, String clientRole, String firstName, String lastName, String cin, String position, String email, String search) {
       /* validatePageable(pageNumber, pageSize);
        Sort sort = Sort.by(buildOrders(sortFields, sortOrders));
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Employee filter = new Employee();
        filter.setActive(state);
        Optional.ofNullable(username).ifPresent(filter::setUsername);
        Optional.ofNullable(clientRole).ifPresent(filter::setClientRole);
        Optional.ofNullable(firstName).ifPresent(filter::setFirstName);
        Optional.ofNullable(lastName).ifPresent(filter::setLastName);
        Optional.ofNullable(cin).ifPresent(filter::setCin);
        Optional.ofNullable(position).ifPresent(filter::setPosition);
        Optional.ofNullable(email).ifPresent(filter::setEmail);

        Page<Employee> employees;
        if (search != null && !search.isEmpty()) {
            employees = findEmployeesByKeyword(search, state, pageable);
        } else {
            employees = getEmployeesPage(filter, pageable);
        }
        Map<String, Object> response = prepareResponse(pageSize, employees);*/

        return ResponseEntity.ok(null);
    }

    @Override
    public Employee createEmployee(Employee employee)  {

        log.info("------------------");
        log.info("token {}",getCurrentAuthToken());
        log.info("-----------------------");
        log.info("start create Employee");
        // Create a user in Keycloak
        keycloakService.createUserInKeycloak(getCurrentAuthToken(), employee);
        // Hash Employee password
        String hashedPassword = passwordEncoder.encode(employee.getMotDePasse());
       String password = employee.getMotDePasse();
        employee.setMotDePasse(hashedPassword);

        // Save Employee


        // Save the employee's avatar to the document service


        return employeeRepository.save(employee);
    }

    @Override
    public ResponseEntity<?> resetEmployeePassword(String username) {
        try {
            String token = keycloakService.getKeycloakAccessToken();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedUsername = authentication.getName();
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_client-admin"));
            if (isAdmin || authenticatedUsername.equals(username)) {
                String newPassword = generateSecureRandomPassword();
                keycloakService.resetUserPasswordInKeycloak(token, username, newPassword);

                return ResponseEntity.ok("Pouvez-vous vérifier votre boite mail pour le nouveau mot de passe.");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Vous n'êtes pas autorisé à réinitialiser le mot de passe de cet employé.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Le mot de passe n'a pas pu être réinitialisé : " + e.getMessage());
        }
    }

    @Override
    public Employee updateEmployee(String username, Employee employee) {

        //Employee existingEmployee = employeeRepository.findByUsername(username).orElseThrow(() -> new EmployeeNotFoundException("Employé introuvable avec le nom d'utilisateur: " + username));

       /* if (employee.getClientRole() != null) {
            keycloakService.verifyClientRole(employee);
            existingEmployee.setClientRole(employee.getClientRole());
        }
        if (employee.getFirstName() != null) {
            existingEmployee.setFirstName(employee.getFirstName());
        }
        if (employee.getLastName() != null) {
            existingEmployee.setLastName(employee.getLastName());
        }
        if (employee.getCin() != null && !employee.getCin().equals(existingEmployee.getCin())) {
            existingEmployee.setCin(employee.getCin());
        }
        if (employee.getDateOfBirth() != null) {
            existingEmployee.setDateOfBirth(employee.getDateOfBirth());
        }
        if (employee.getAddress() != null) {
            existingEmployee.setAddress(employee.getAddress());
        }
        if (employee.getEmail() != null && !employee.getEmail().equals(existingEmployee.getEmail())) {
            existingEmployee.setEmail(employee.getEmail());
        }
        if (employee.getPhoneNumber() != null && !employee.getPhoneNumber().equals(existingEmployee.getPhoneNumber())) {
            existingEmployee.setPhoneNumber(employee.getPhoneNumber());
        }
        if (employee.getPosition() != null) {
            existingEmployee.setPosition(employee.getPosition());
        }
        if (employee.getHireDate() != null) {
            existingEmployee.setHireDate(employee.getHireDate());
        }
        if (employee.getLeaveDate() != null) {
            existingEmployee.setLeaveDate(employee.getLeaveDate());
        }
        if (employee.getSalary() != null) {
            existingEmployee.setSalary(employee.getSalary());
        }
        if (employee.getDepartment() != null) {
            existingEmployee.setDepartment(employee.getDepartment());
        }
        if (employee.getLeaveRate() != null) {
            existingEmployee.setLeaveRate(employee.getLeaveRate());
        }
        if (employee.getResponsible() != null) {
            existingEmployee.setResponsible(employee.getResponsible());
        }
        if (employee.getImage() != null) {
            String avatarUrl = saveAvatarToDocumentService(employee, existingEmployee, "update");
            existingEmployee.setAvatar(avatarUrl);
        }
        existingEmployee.setPassword(existingEmployee.getPassword());
        EmployeeResponse employeeResponse = employeeMapper.convertToResponse(existingEmployee);
        kafkaTemplate.send("topic-update-employee", employeeResponse);

        employeeRepository.save(existingEmployee);
        keycloakService.updateUserInKeycloak(keycloakService.getKeycloakAccessToken(), employee);*/

        return null;
    }

    public Employee updateEmployeeByEmployee(String username, Employee employee) {

        /*Employee existingEmployee = employeeRepository.findByUsername(username).orElseThrow(() -> new EmployeeNotFoundException("Employé introuvable avec le nom d'utilisateur: " + username));


        if (employee.getFirstName() != null) {
            existingEmployee.setFirstName(employee.getFirstName());
        }
        if (employee.getLastName() != null) {
            existingEmployee.setLastName(employee.getLastName());
        }
        if (employee.getCin() != null && !employee.getCin().equals(existingEmployee.getCin())) {
            existingEmployee.setCin(employee.getCin());
        }
        if (employee.getDateOfBirth() != null) {
            existingEmployee.setDateOfBirth(employee.getDateOfBirth());
        }
        if (employee.getAddress() != null) {
            existingEmployee.setAddress(employee.getAddress());
        }
        if (employee.getEmail() != null && !employee.getEmail().equals(existingEmployee.getEmail())) {
            existingEmployee.setEmail(employee.getEmail());
        }
        if (employee.getPhoneNumber() != null && !employee.getPhoneNumber().equals(existingEmployee.getPhoneNumber())) {
            existingEmployee.setPhoneNumber(employee.getPhoneNumber());
        }
        if (employee.getImage() != null) {
            String avatarUrl = saveAvatarToDocumentService(employee, existingEmployee, "update");
            existingEmployee.setAvatar(avatarUrl);
        }
        existingEmployee.setPassword(existingEmployee.getPassword());
        EmployeeResponse employeeResponse = employeeMapper.convertToResponse(existingEmployee);
        kafkaTemplate.send("topic-update-employee", employeeResponse);
        employeeRepository.save(existingEmployee);
        return existingEmployee;*/
        return  null;
    }
    @Override
    public void deleteEmployeeByUsername(String username) {
      /*  Employee existingEmployee = employeeRepository.findByUsername(username).orElseThrow(() -> new EmployeeNotFoundException("Employé introuvable avec le nom d'utilisateur: " + username));
        EmployeeResponse employeeResponse = employeeMapper.convertToResponse(existingEmployee);
        kafkaTemplate.send("topic-delete-employee", employeeResponse);
        employeeRepository.deleteById(username);
        keycloakService.deleteUserFromKeycloak(keycloakService.getKeycloakAccessToken(), username);*/
    }
    @Override
    public Employee getEmployeeByUsername(String username) {
        //return employeeRepository.findByUsername(username).orElseThrow(() -> new EmployeeNotFoundException("Employé introuvable avec le nom d'utilisateur: " + username));
        return  null;
    }

    @Override
    public Employee activateEmployee(String username, Boolean state) {
       /* Employee existingEmployee = employeeRepository.findByUsername(username).orElseThrow(() -> new EmployeeNotFoundException("Employé introuvable avec le nom d'utilisateur: " + username));
        existingEmployee.setActive(state);
        employeeRepository.save(existingEmployee);;
        keycloakService.setUserEnabledStatus(keycloakService.getKeycloakAccessToken(), username, state);
        EmployeeResponse employeeResponse = employeeMapper.convertToResponse(existingEmployee);
        kafkaTemplate.send("topic-archive-employee", employeeResponse);
        return existingEmployee;*/
        return null;
    }

    private String saveAvatarToDocumentService(Employee employee, Employee createdEmployee, String type) {
      /*  DocumentDTO documentDto = createDocumentDto(employee, createdEmployee);
        DocumentResponseDTO response;

        try {
            if ("create".equals(type)) {
                response = documentServiceClient.addAvatar(getCurrentAuthToken(), documentDto);
            } else {
                response = documentServiceClient.updateAvatar(getCurrentAuthToken(), documentDto);
            }

            if (response != null) {
                return response.getFilePath();
            } else {
                logAndThrow("Impossible d'enregistrer l'avatar dans le service de document.");
            }
        } catch (Exception e) {
            logAndThrow("Une exception s'est produite lors de l'enregistrement de l'avatar dans le service de document.: " + e.getMessage());
        }

        return null;*/
        return null;
    }

    private void logAndThrow(String message) {
        // Optionally, you can log the error here
        throw new RuntimeException(message);
    }

    private String generateSecureRandomPassword() {
       return RandomStringUtils.randomAlphanumeric(8);
    }


    /*  private DocumentDTO createDocumentDto(Employee employee, Employee createdEmployee) {
        DocumentDTO documentDto = new DocumentDTO();
        documentDto.setFile(employee.getImage());
        documentDto.setUsername(createdEmployee.getUsername());
        documentDto.setTitle(createdEmployee.getUsername());
        documentDto.setReference("avatar");
        return documentDto;
    }*/

    private String getCurrentAuthToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getCredentials();
        return "Bearer " + jwt.getTokenValue();
    }
/*
    private Map<String, Object> prepareResponse(int pageSize, Page<Employee> employees) {
        List<EmployeeDTO> employeeDTOList = employees.getContent().stream().map(employeeMapper::convertToDTO).collect(Collectors.toList());
        Map<String, Object> response = new HashMap<>();
        response.put("data", employeeDTOList);
        response.put("currentPage", employees.getNumber());
        response.put("pageSize", pageSize);
        response.put("totalPages", employees.getTotalPages());
        response.put("totalElements", employees.getTotalElements());

        if (employees.isEmpty()) {
            response.put("message", "Aucun employé correspondant aux critères fournis n'a été trouvé.");
        }
        return response;
    }

    private Page<Employee> findEmployeesByExample(Employee filter, Pageable pageable) {
        ExampleMatcher matcher = ExampleMatcher.matchingAll().withIgnoreCase().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Employee> example = Example.of(filter, matcher);
        return employeeRepository.findAll(example, pageable);
    }

    private void validatePageable(int pageNumber, int pageSize) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Le numéro de page doit être une valeur positive.");
        }
        if (pageSize < 1 || pageSize > 100) {
            throw new IllegalArgumentException("La taille de la page doit être comprise entre 1 et 100.");
        }
    }

    private Page<Employee> getEmployeesPage(Employee filter, Pageable pageable) {
        if (filter != null) {
            return findEmployeesByExample(filter, pageable);
        }
        return employeeRepository.findAll(pageable);
    }

    private void validateUniqueFields(Employee employee) {
        if (employeeRepository.existsByUsername(employee.getUsername())) {
            throw new IllegalArgumentException("Le nom d'utilisateur doit être unique.");
        }
        if (employeeRepository.existsByCin(employee.getCin())) {
            throw new IllegalArgumentException("Le CIN doit être unique.");
        }

        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new IllegalArgumentException("L'adresse e-mail doit être unique.");
        }

        if (employeeRepository.existsByPhoneNumber(employee.getPhoneNumber())) {
            throw new IllegalArgumentException("Le numéro de téléphone doit être unique.");
        }
    }*/

    @Override
    public Sort.Order[] buildOrders(String[] sortFields, String[] sortOrders) {
        Sort.Order[] orders = new Sort.Order[sortFields.length];
        for (int i = 0; i < sortFields.length; i++) {
            Sort.Direction direction = sortOrders[i].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            orders[i] = new Sort.Order(direction, sortFields[i]);
        }
        return orders;
    }


/*

    private Page<Employee> findEmployeesByKeyword(String search, Boolean state, Pageable pageable) {
        return employeeRepository.findActiveEmployeesByKeyword(search, state, pageable);
    }*/


}
