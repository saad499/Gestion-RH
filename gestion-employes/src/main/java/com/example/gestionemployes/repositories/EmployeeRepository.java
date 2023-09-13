package com.example.gestionemployes.repositories;

import com.example.gestionemployes.entities.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,String> {

    Optional<Employee> findByUsername(String username);

    @Query("select e from Employee e where e.nom like %:nom% and e.supprimer = false order by e.dateEmbauche desc")
    Page<Employee> findEmployeeByNom(@Param("nom") String keyword, Pageable pageable);

    @Query("select e from Employee e where e.supprimer = false order by e.dateEmbauche desc")
    Page<Employee> findEmployeeNoSupprimer(Pageable pageable);
}
