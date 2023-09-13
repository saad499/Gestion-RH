package com.example.gestionemployes.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class DepartmentEmployeeCount {
    private Long id;
    private String departementName;
    private Long employeeCount;
}
