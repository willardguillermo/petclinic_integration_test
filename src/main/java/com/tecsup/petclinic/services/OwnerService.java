package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.OwnerDTO;
import com.tecsup.petclinic.exceptions.OwnerNotFoundException;

import java.util.List;

public interface OwnerService {

    OwnerDTO create(OwnerDTO ownerTO);

    OwnerDTO update(OwnerDTO ownerTO);

    void delete(Integer id) throws OwnerNotFoundException;

    OwnerDTO findById(Integer id) throws OwnerNotFoundException;

    List<OwnerDTO> findByLastName(String lastName);

    List<OwnerDTO> findAll();
}