package com.tecsup.petclinic.util;

import com.tecsup.petclinic.dtos.OwnerDTO;

import java.util.List;

/**
 * Objetos Owner para las pruebas con Mockito (datos iguales a data.sql).
 */
public class OwnerTObjectCreator {

    public static OwnerDTO getOwnerTO() {
        return OwnerDTO.builder().id(1).firstName("George").lastName("Franklin")
                .address("110 W. Liberty St.").city("Madison").telephone("6085551023").build();
    }

    public static List<OwnerDTO> getOwnersTO() {
        return List.of(
                getOwnerTO(),
                OwnerDTO.builder().id(2).firstName("Betty").lastName("Davis")
                        .address("638 Cardinal Ave.").city("Sun Prairie").telephone("6085551749").build(),
                OwnerDTO.builder().id(3).firstName("Eduardo").lastName("Rodriquez")
                        .address("2693 Commerce St.").city("McFarland").telephone("6085558763").build());
    }

    public static List<OwnerDTO> getOwnersDavisTO() {
        return List.of(
                OwnerDTO.builder().id(2).firstName("Betty").lastName("Davis")
                        .address("638 Cardinal Ave.").city("Sun Prairie").telephone("6085551749").build(),
                OwnerDTO.builder().id(4).firstName("Harold").lastName("Davis")
                        .address("563 Friendly St.").city("Windsor").telephone("6085553198").build());
    }

    public static OwnerDTO newOwnerTO() {
        return OwnerDTO.builder().firstName("Alexander").lastName("Ramos")
                .address("Av. Cascanueces 2221").city("Lima").telephone("987654321").build();
    }

    public static OwnerDTO newOwnerCreatedTO() {
        OwnerDTO owner = newOwnerTO();
        owner.setId(11);
        return owner;
    }
}