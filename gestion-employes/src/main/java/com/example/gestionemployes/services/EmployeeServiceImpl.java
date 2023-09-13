package com.example.gestionemployes.services;

import com.example.gestionemployes.entities.Employee;
import com.example.gestionemployes.exception.EmployeeNotFoundException;
import com.example.gestionemployes.exception.IllegalArgumentException;
import com.example.gestionemployes.repositories.EmployeeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {
    private EmployeeRepository employeeRepository;
    private KeycloakService keycloakService;
    public EmployeeServiceImpl(EmployeeRepository employeeRepository, KeycloakService keycloakService){
        this.employeeRepository = employeeRepository;
        this.keycloakService = keycloakService;
    }
    @Override
    public boolean isValidRole(String Role){
        return Arrays.asList("MANAGER", "EMPLOYEE", "ADMIN", "RESPONSABLE-RH").contains(Role);
    }
    @Override
    public Employee createEmployee(Employee employee) {
        employee.setCreatedAt(new Date());
        String Role = employee.getRole();
        if(!isValidRole(Role)){
            throw new IllegalArgumentException("Le role n'est pas valide");
        }
        keycloakService.registerEmployeeInKeycloak(employee);
        Employee saveEmployee = employeeRepository.save(employee);
        return saveEmployee;
    }

    @Override
    public Page<Employee> listEmployeeSupprimer(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> listEmployeNoSup = employeeRepository.findEmployeeNoSupprimer(pageable);
        return listEmployeNoSup;
    }

    @Override
    public Employee getEmployeeByUsername(String username) throws EmployeeNotFoundException, IllegalArgumentException {
        Optional<Employee> employeeOptional = employeeRepository.findByUsername(username);
        if(employeeOptional.isPresent()){
            return employeeOptional.get();
        } else{
            throw new EmployeeNotFoundException("Employee " + username + " n'exist pas");
        }
    }

    @Override
    public Employee updateEmployee(String username,Employee employee) throws EmployeeNotFoundException {

        Employee updateEmployees = employeeRepository.findByUsername(username).orElseThrow(()->new EmployeeNotFoundException("Employee " + username + " n'existe pas"));
        BeanUtils.copyProperties(employee, updateEmployees);
        updateEmployees.setUpdatedAt(new Date());
        String Role = employee.getRole();
        if(!isValidRole(Role)){
            throw new IllegalArgumentException("Le role n'est pas valide");
        }
        Employee updateEmployee = employeeRepository.save(updateEmployees);
        return updateEmployee;
    }

    @Override
    public void deleteEmployee(String username) {

    }

    @Override
    public Employee cacherEmployee(Employee employee) {
        Employee cacherEmployee = employeeRepository.findByUsername(employee.getUsername()).orElseThrow();
        employee.setSupprimer(true);
        employeeRepository.save(cacherEmployee);
        return cacherEmployee;
    }

    @Override
    public Page<Employee> chercherEmployee(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        Page<Employee> employees = employeeRepository.findEmployeeByNom(keyword, pageable);
        return employees;
    }
}
