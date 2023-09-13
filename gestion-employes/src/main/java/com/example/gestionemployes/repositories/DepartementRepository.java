package com.example.gestionemployes.repositories;

import com.example.gestionemployes.entities.Departement;
import com.example.gestionemployes.entities.DepartmentEmployeeCount;
import jdk.jfr.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartementRepository extends JpaRepository<Departement,Long> {

    @Query("select d from Departement d where d.name like %:name% and d.supprimer = false order by d.id desc")
    Page<Departement> findDepartementByName(@Param("name") String keyword, Pageable pageable);

    @Query("select d from Departement d where d.supprimer = false order by d.id desc")
    Page<Departement> findDepartementNoSupprimer(Pageable pageable);

    @Query("SELECT new com.example.gestionemployes.entities.DepartmentEmployeeCount(d.id,d.name, COUNT(e.username)) " +
            "FROM Departement d LEFT JOIN d.employees e " +
            "GROUP BY d.id, d.name")
    List<DepartmentEmployeeCount> getDepartmentEmployeeCounts();
}
