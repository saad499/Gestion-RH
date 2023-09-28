package com.example.gestionemployes.services;


import com.example.gestionemployes.entities.Employee;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public interface EmployeeService {
    Employee createEmployee(Employee employee);

    ResponseEntity<Map<String, Object>> getAllEmployees(int pageNumber, int pageSize, Boolean state, String[] sortFields, String[] sortOrders, String username, String clientRole, String firstName, String lastName, String cin, String position, String email, String search);

    void deleteEmployeeByUsername(String username);

    Employee updateEmployee(String username, Employee employee);
    Employee updateEmployeeByEmployee(String username, Employee employee);

    Employee getEmployeeByUsername(String username);

    Employee activateEmployee(String username, Boolean state);

    Sort.Order[] buildOrders(String[] sortFields, String[] sortOrders);

    ResponseEntity<?> resetEmployeePassword(String username);



}
