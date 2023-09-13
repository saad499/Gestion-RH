package com.example.gestionemployes.exception;

public class DepartementNotFoundException extends RuntimeException{
    public DepartementNotFoundException(String message){
        super(message);
    }
}
