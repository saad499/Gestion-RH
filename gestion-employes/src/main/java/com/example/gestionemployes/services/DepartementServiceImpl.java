package com.example.gestionemployes.services;

import com.example.gestionemployes.entities.Departement;
import com.example.gestionemployes.entities.DepartmentEmployeeCount;
import com.example.gestionemployes.exception.DepartementNotFoundException;
import com.example.gestionemployes.exception.IllegalArgumentException;
import com.example.gestionemployes.repositories.DepartementRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class DepartementServiceImpl implements DepartementService{
    private final DepartementRepository departementRepository;

    @Autowired
    public DepartementServiceImpl(DepartementRepository departementRepository){
        this.departementRepository = departementRepository;
    }

    @Override
    public Departement createDepartement(Departement departement) {
        departement.setCreatedAt(new Date());
        Departement saveDepartement = departementRepository.save(departement);
        return saveDepartement;
    }

    @Override
    public Page<Departement> getAllDepartement(Pageable pageable) {
        return departementRepository.findAll(pageable);
    }

    @Override
    public Departement getDepartementById(Long id) throws DepartementNotFoundException, IllegalArgumentException {
        if(id<=0 || !String.valueOf(id).matches("\\d+")){
            throw new IllegalArgumentException("Departement invalid Id"+id);
        }
        return departementRepository.findById(id)
                .orElseThrow(() -> new DepartementNotFoundException("Departement " + id + " n'existe pas"));
    }

    @Override
    public Departement updateDepartement(Long id, Departement departement) throws DepartementNotFoundException {
        Departement departements = departementRepository.findById(id).orElseThrow(() -> new DepartementNotFoundException("Departement " + id + " n'existe pas"));
        BeanUtils.copyProperties(departement,departements);
        departements.setUpdatedAt(new Date());

        Departement updateDepartement = departementRepository.save(departements);
        return updateDepartement;
    }
    /*@Override
    public Departement updateDepartement(Departement departement) throws DepartementNotFoundException {
        Departement departements = departementRepository.findById(departement.getId()).orElseThrow(() -> new DepartementNotFoundException("Departement " + id + " n'existe pas"));
        BeanUtils.copyProperties(departement,departements);
        departements.setUpdatedAt(new Date());

        Departement updateDepartement = departementRepository.save(departements);
        return updateDepartement;
    }*/

    @Override
    public void deleteDepartement(Long id) {
        Departement departement = getDepartementById(id);
        departementRepository.delete(departement);
    }

    @Override
    public Departement cacherDepartement(Departement departement) {
        Departement departements = departementRepository.findById(departement.getId()).orElseThrow();
        departements.setSupprimer(true);
        departementRepository.save(departements);
        return departements;
    }

    @Override
    public Page<Departement> chercherDepartement(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Departement> departement = departementRepository.findDepartementByName(keyword,pageable);
        return departement;
    }

    @Override
    public Page<Departement> listDepartementSupprimer(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Departement> listDepartementSup = departementRepository.findDepartementNoSupprimer(pageable);
        return listDepartementSup;
    }

    @Override
    public List<DepartmentEmployeeCount> getDepartmentEmployeeCount() {
        return departementRepository.getDepartmentEmployeeCounts();
    }



}
