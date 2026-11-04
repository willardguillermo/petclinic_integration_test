package com.tecsup.petclinic.mapper;

import com.tecsup.petclinic.dtos.SpecialtyDTO;
import com.tecsup.petclinic.entities.Specialty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SpecialtyMapper {

    public SpecialtyDTO mapToDto(Specialty specialty) {
        SpecialtyDTO dto = new SpecialtyDTO();

        dto.setId(specialty.getId());
        dto.setName(specialty.getName());
        dto.setOffice(specialty.getOffice());
        dto.setHOpen(specialty.getHOpen());
        dto.setHClose(specialty.getHClose());

        return dto;
    }

    public Specialty mapToEntity(SpecialtyDTO dto) {
        Specialty specialty = new Specialty();

        specialty.setId(dto.getId());
        specialty.setName(dto.getName());
        specialty.setOffice(dto.getOffice());
        specialty.setHOpen(dto.getHOpen());
        specialty.setHClose(dto.getHClose());

        return specialty;
    }

    public List<SpecialtyDTO> mapToDtoList(List<Specialty> specialties) {
        return specialties.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
}