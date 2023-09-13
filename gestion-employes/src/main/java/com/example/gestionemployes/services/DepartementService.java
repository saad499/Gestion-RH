package com.example.gestionemployes.services;

import com.example.gestionemployes.entities.Departement;
import com.example.gestionemployes.entities.DepartmentEmployeeCount;
import com.example.gestionemployes.exception.DepartementNotFoundException;
import com.example.gestionemployes.exception.IllegalArgumentException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DepartementService {
    Departement createDepartement(Departement departement);
    Page<Departement> getAllDepartement(Pageable pageable);
    Departement getDepartementById(Long id) throws DepartementNotFoundException, IllegalArgumentException;
//    Departement updateDepartement(Long id, Departement departement) throws DepartementNotFoundException;
    Departement updateDepartement(Long id, Departement departement) throws DepartementNotFoundException;

    void deleteDepartement(Long id);
    Departement cacherDepartement(Departement departement);
    Page<Departement> chercherDepartement(String keyword, int page, int size);
    Page<Departement> listDepartementSupprimer(int page, int size);
    List<DepartmentEmployeeCount> getDepartmentEmployeeCount();

}
