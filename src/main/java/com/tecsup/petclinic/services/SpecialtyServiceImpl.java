package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.SpecialtyDTO;
import com.tecsup.petclinic.entities.Specialty;
import com.tecsup.petclinic.exceptions.SpecialtyNotFoundException;
import com.tecsup.petclinic.mapper.SpecialtyMapper;
import com.tecsup.petclinic.repositories.SpecialtyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;
    private final SpecialtyMapper specialtyMapper;

    public SpecialtyServiceImpl(
            SpecialtyRepository specialtyRepository,
            SpecialtyMapper specialtyMapper) {
        this.specialtyRepository = specialtyRepository;
        this.specialtyMapper = specialtyMapper;
    }

    @Override
    public List<SpecialtyDTO> findAll() {
        return specialtyMapper.mapToDtoList(specialtyRepository.findAll());
    }

    @Override
    public SpecialtyDTO findById(Integer id) throws SpecialtyNotFoundException {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() ->
                        new SpecialtyNotFoundException("Specialty not found"));

        return specialtyMapper.mapToDto(specialty);
    }

    @Override
    public SpecialtyDTO create(SpecialtyDTO specialtyDTO) {
        Specialty specialty = specialtyMapper.mapToEntity(specialtyDTO);
        Specialty saved = specialtyRepository.save(specialty);

        return specialtyMapper.mapToDto(saved);
    }

    @Override
    public List<SpecialtyDTO> findByName(String name) {
        return specialtyMapper.mapToDtoList(
                specialtyRepository.findByName(name));
    }

    @Override
    public List<SpecialtyDTO> findByOffice(String office) {
        return specialtyMapper.mapToDtoList(
                specialtyRepository.findByOffice(office));
    }

    @Override
    public void delete(Integer id) throws SpecialtyNotFoundException {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() ->
                        new SpecialtyNotFoundException("Specialty not found"));

        specialtyRepository.delete(specialty);
    }
}