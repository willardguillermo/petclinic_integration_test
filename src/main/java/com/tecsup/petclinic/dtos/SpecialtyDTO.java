package com.tecsup.petclinic.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SpecialtyDTO {

    private Integer id;
    private String name;
    private String office;
    private Integer hOpen;
    private Integer hClose;
}