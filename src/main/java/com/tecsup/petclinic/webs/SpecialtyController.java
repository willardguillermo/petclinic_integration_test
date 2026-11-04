package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.SpecialtyDTO;
import com.tecsup.petclinic.exceptions.SpecialtyNotFoundException;
import com.tecsup.petclinic.services.SpecialtyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    @GetMapping("/specialties")
    public ResponseEntity<List<SpecialtyDTO>> findAllSpecialties(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String office) {

        if (name != null) {
            return ResponseEntity.ok(specialtyService.findByName(name));
        }

        if (office != null) {
            return ResponseEntity.ok(specialtyService.findByOffice(office));
        }

        return ResponseEntity.ok(specialtyService.findAll());
    }

    @GetMapping("/specialties/{id}")
    public ResponseEntity<SpecialtyDTO> findById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(specialtyService.findById(id));
        } catch (SpecialtyNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/specialties")
    public ResponseEntity<SpecialtyDTO> create(
            @RequestBody SpecialtyDTO specialtyDTO) {

        if (specialtyDTO.getHOpen() == null ||
                specialtyDTO.getHClose() == null ||
                specialtyDTO.getHOpen() >= specialtyDTO.getHClose()) {
            return ResponseEntity.badRequest().build();
        }

        if (specialtyDTO.getHOpen() < 0 ||
                specialtyDTO.getHOpen() > 23 ||
                specialtyDTO.getHClose() < 0 ||
                specialtyDTO.getHClose() > 23) {
            return ResponseEntity.badRequest().build();
        }

        SpecialtyDTO created = specialtyService.create(specialtyDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/specialties/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        try {
            specialtyService.delete(id);
            return ResponseEntity.ok("Delete ID: " + id);
        } catch (SpecialtyNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}