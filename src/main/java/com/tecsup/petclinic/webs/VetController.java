package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.exceptions.VetNotFoundException;
import com.tecsup.petclinic.services.VetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class VetController {

    private final VetService vetService;

    public VetController(VetService vetService) {
        this.vetService = vetService;
    }

    /**
     * Listar todos los veterinarios
     */
    @GetMapping(value = "/vets")
    public ResponseEntity<List<VetDTO>> findAllVets() {
        List<VetDTO> vets = vetService.findAll();
        vets.forEach(item -> log.info("Vet >> {}", item));
        return ResponseEntity.ok(vets);
    }

    /**
     * Crear veterinario
     */
    @PostMapping(value = "/vets")
    public ResponseEntity<VetDTO> create(@RequestBody VetDTO vetDTO) {
        VetDTO newVetDTO = vetService.create(vetDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newVetDTO);
    }

    /**
     * Buscar veterinario por id
     */
    @GetMapping(value = "/vets/{id}")
    public ResponseEntity<VetDTO> findById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(vetService.findById(id));
        } catch (VetNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Actualizar veterinario
     */
    @PutMapping(value = "/vets/{id}")
    public ResponseEntity<VetDTO> update(@RequestBody VetDTO vetDTO, @PathVariable Integer id) {
        try {
            return ResponseEntity.ok(vetService.update(id, vetDTO));
        } catch (VetNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Eliminar veterinario
     */
    @DeleteMapping(value = "/vets/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        try {
            vetService.delete(id);
            return ResponseEntity.ok(" Delete ID :" + id);
        } catch (VetNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}