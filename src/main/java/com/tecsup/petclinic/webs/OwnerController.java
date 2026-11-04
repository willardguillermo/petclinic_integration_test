package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.OwnerDTO;
import com.tecsup.petclinic.exceptions.OwnerNotFoundException;
import com.tecsup.petclinic.services.OwnerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class OwnerController {

    private final OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    /**
     * GET /owners           -> todos los dueños
     * GET /owners?lastName= -> dueños con ese apellido
     */
    @GetMapping(value = "/owners")
    public ResponseEntity<List<OwnerDTO>> findAll(@RequestParam(name = "lastName", required = false) String lastName) {

        List<OwnerDTO> owners = (lastName == null)
                ? ownerService.findAll()
                : ownerService.findByLastName(lastName);

        return ResponseEntity.ok(owners);
    }

    @PostMapping(value = "/owners")
    public ResponseEntity<OwnerDTO> create(@RequestBody OwnerDTO ownerTO) {

        if (ownerTO.getLastName() == null || ownerTO.getLastName().isBlank())
            return ResponseEntity.badRequest().build();

        OwnerDTO newOwnerTO = ownerService.create(ownerTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newOwnerTO);
    }

    @GetMapping(value = "/owners/{id}")
    public ResponseEntity<OwnerDTO> findById(@PathVariable("id") Integer id) {
        try {
            return ResponseEntity.ok(ownerService.findById(id));
        } catch (OwnerNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping(value = "/owners/{id}")
    public ResponseEntity<OwnerDTO> update(@RequestBody OwnerDTO ownerTO, @PathVariable("id") Integer id) {
        try {
            OwnerDTO current = ownerService.findById(id);

            current.setFirstName(ownerTO.getFirstName());
            current.setLastName(ownerTO.getLastName());
            current.setAddress(ownerTO.getAddress());
            current.setCity(ownerTO.getCity());
            current.setTelephone(ownerTO.getTelephone());

            return ResponseEntity.ok(ownerService.update(current));
        } catch (OwnerNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(value = "/owners/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Integer id) {
        try {
            ownerService.delete(id);
            return ResponseEntity.ok(" Delete ID :" + id);
        } catch (OwnerNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}