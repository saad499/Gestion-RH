package com.example.gestionemployes.services;

import com.example.gestionemployes.entities.Employee;
import com.example.gestionemployes.exception.EmployeeNotFoundException;
import com.example.gestionemployes.exception.IllegalArgumentException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface EmployeeService {
    Employee createEmployee(Employee employee);
    Page<Employee> listEmployeeSupprimer(int page,int size);
    Employee getEmployeeByUsername(String username) throws EmployeeNotFoundException, IllegalArgumentException;
    Employee updateEmployee(String username,Employee employee) throws EmployeeNotFoundException;
    void deleteEmployee(String username);
    Employee cacherEmployee(Employee employee);
    Page<Employee> chercherEmployee(String keyword, int page, int size);
    boolean isValidRole(String Role);
}
