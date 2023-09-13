package com.example.gestionemployes.web;

import com.example.gestionemployes.entities.Departement;
import com.example.gestionemployes.entities.DepartmentEmployeeCount;
import com.example.gestionemployes.entities.PaginatedResponse;
import com.example.gestionemployes.exception.DepartementNotFoundException;
import com.example.gestionemployes.exception.ErrorResponse;
import com.example.gestionemployes.exception.IllegalArgumentException;
import com.example.gestionemployes.services.DepartementService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departements")
public class DepartementController {
    private final DepartementService departementService;

    @Autowired
    public DepartementController(DepartementService departementService){
        this.departementService = departementService;
    }

    @PostMapping
    public ResponseEntity<?> createDepartement(@RequestBody @Valid Departement departement, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            StringBuilder errorMessage = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> {
                errorMessage.append(error.getDefaultMessage()).append(" .");
            });
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        Departement saveDepartement = departementService.createDepartement(departement);
        return ResponseEntity.ok(saveDepartement);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<Departement>> getAllDepartement(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "5") int size){
        //Pageable pageable = PageRequest.of(page, size);
        Page<Departement> departementPage = departementService.listDepartementSupprimer(page, size);

        PaginatedResponse<Departement> response = new PaginatedResponse<>();
        response.setContent(departementPage.getContent());
        response.setPagenumber(departementPage.getNumber());
        response.setPage(departementPage.getSize());
        response.setTotalElements((int) departementPage.getTotalElements());
        response.setTotalPages(departementPage.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public Departement getDepartementById(@PathVariable(name = "id") String id) throws DepartementNotFoundException, IllegalArgumentException {
        try{
            Long departementIdLong = Long.parseLong(id);
            if (departementIdLong <= 0){
                throw new IllegalArgumentException("invalid departement Id : " + id);
            }
            Departement departement = departementService.getDepartementById(departementIdLong);
            if(departement == null){
                throw new DepartementNotFoundException("Departement not found with id : " + departementIdLong);
            }
            return departement;
        }catch (NumberFormatException e){
            throw new IllegalArgumentException("Invalid departement id : " + id);
        }

    }

    /**/@PutMapping("/{id}")
    public ResponseEntity<?> updateDepartement(@PathVariable(name="id") Long id, @RequestBody Departement departement) {
        try{
            departement.setId(id);
            Departement updateDepartement = departementService.updateDepartement(id,departement);
            return ResponseEntity.ok(updateDepartement);
        }catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Le département n'a pas été trouvé.");
        }catch (ValidationException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreurinterne s'est produite.");
        }

    }

   /* @PutMapping("/{id}")
    public Departement updateCDepartement(@PathVariable(name="id") Long id, @RequestBody Departement departement){
        departement.setId(id);
        return departementService.updateDepartement(departement);
    }*/

    @DeleteMapping("/{id}")
    public void deleteDepartement(@PathVariable Long id) {
        departementService.deleteDepartement(id);
    }

    @GetMapping("/chercher")
    public ResponseEntity<Page<Departement>> chercherDepartement(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ){
        Page<Departement> departements = departementService.chercherDepartement(keyword, page, size);
        return ResponseEntity.ok(departements);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateSupDepartement(@PathVariable(name = "id") Long id, @RequestBody Departement departement){
        try{
            departement.setId(id);
            Departement departements = departementService.cacherDepartement(departement);
            if(departement == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Le département avec l'ID spécifié n'existe pas.");
            }
            return ResponseEntity.ok(departements);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur interne s'est produite.");
        }

    }

    @GetMapping("/nombre-employees")
    public List<DepartmentEmployeeCount> getDepartmentEmployeeCount(){
        return departementService.getDepartmentEmployeeCount();
    }

    @ExceptionHandler(DepartementNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleDepartementNotFoundException(DepartementNotFoundException ex){
        return new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex){
        return new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
