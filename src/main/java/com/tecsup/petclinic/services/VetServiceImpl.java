package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.entities.Vet;
import com.tecsup.petclinic.exceptions.VetNotFoundException;
import com.tecsup.petclinic.mapper.VetMapper;
import com.tecsup.petclinic.repositories.VetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class VetServiceImpl implements VetService {

    private final VetRepository vetRepository;
    private final VetMapper vetMapper;

    public VetServiceImpl(VetRepository vetRepository, VetMapper vetMapper) {
        this.vetRepository = vetRepository;
        this.vetMapper = vetMapper;
    }

    @Override
    public List<VetDTO> findAll() {
        return vetMapper.mapToDtoList(vetRepository.findAll());
    }

    @Override
    public VetDTO findById(Integer id) throws VetNotFoundException {
        Vet vet = vetRepository.findById(id)
                .orElseThrow(() -> new VetNotFoundException("Vet not found...!"));
        return vetMapper.mapToDto(vet);
    }

    @Override
    public VetDTO create(VetDTO vetDTO) {
        if (vetDTO.getActive() == null) {
            vetDTO.setActive(true);   // por defecto un veterinario nuevo está activo
        }
        Vet newVet = vetRepository.save(vetMapper.mapToEntity(vetDTO));
        return vetMapper.mapToDto(newVet);
    }

    @Override
    public VetDTO update(Integer id, VetDTO vetDTO) throws VetNotFoundException {
        Vet vet = vetRepository.findById(id)
                .orElseThrow(() -> new VetNotFoundException("Vet not found...!"));

        vet.setFirstName(vetDTO.getFirstName());
        vet.setLastName(vetDTO.getLastName());
        vet.setEmail(vetDTO.getEmail());
        vet.setPhone(vetDTO.getPhone());
        if (vetDTO.getActive() != null) {
            vet.setActive(vetDTO.getActive());
        }

        return vetMapper.mapToDto(vetRepository.save(vet));
    }

    @Override
    public void delete(Integer id) throws VetNotFoundException {
        Vet vet = vetRepository.findById(id)
                .orElseThrow(() -> new VetNotFoundException("Vet not found...!"));
        vetRepository.delete(vet);
    }
}