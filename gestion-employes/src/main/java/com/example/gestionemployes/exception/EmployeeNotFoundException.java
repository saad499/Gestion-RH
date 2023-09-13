package com.example.gestionemployes.exception;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(String message){
        super(message);
    }
}
