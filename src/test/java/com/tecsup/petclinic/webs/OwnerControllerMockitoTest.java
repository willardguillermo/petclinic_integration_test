package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.petclinic.dtos.OwnerDTO;
import com.tecsup.petclinic.exceptions.OwnerNotFoundException;
import com.tecsup.petclinic.services.OwnerService;
import com.tecsup.petclinic.util.OwnerTObjectCreator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de OwnerController con el servicio simulado (Mockito): solo controlador y contrato JSON.
 */
@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class OwnerControllerMockitoTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OwnerService ownerService;

    // ---------------- Listado y consulta ----------------

    @Test
    public void testFindAllOwners() throws Exception {

        List<OwnerDTO> ownersTO = OwnerTObjectCreator.getOwnersTO();

        Mockito.when(ownerService.findAll()).thenReturn(ownersTO);

        this.mockMvc.perform(get("/owners"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(ownersTO.size())))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    public void testFindOwnerOK() throws Exception {

        OwnerDTO ownerTO = OwnerTObjectCreator.getOwnerTO();

        Mockito.when(ownerService.findById(ownerTO.getId())).thenReturn(ownerTO);

        mockMvc.perform(get("/owners/1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(ownerTO.getId())))
                .andExpect(jsonPath("$.firstName", is(ownerTO.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(ownerTO.getLastName())));
    }

    @Test
    public void testFindOwnerKO() throws Exception {

        Mockito.when(ownerService.findById(666))
                .thenThrow(new OwnerNotFoundException("Record not found...!"));

        mockMvc.perform(get("/owners/666"))
                .andExpect(status().isNotFound());
    }

    // ---------------- Creación ----------------

    @Test
    public void testCreateOwner() throws Exception {

        OwnerDTO newOwnerTO = OwnerTObjectCreator.newOwnerTO();
        OwnerDTO createdOwnerTO = OwnerTObjectCreator.newOwnerCreatedTO();

        Mockito.when(ownerService.create(any(OwnerDTO.class))).thenReturn(createdOwnerTO);

        mockMvc.perform(post("/owners")
                        .content(om.writeValueAsString(newOwnerTO))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(createdOwnerTO.getId())))
                .andExpect(jsonPath("$.firstName", is(newOwnerTO.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(newOwnerTO.getLastName())))
                .andExpect(jsonPath("$.city", is(newOwnerTO.getCity())));
    }

    @Test
    public void testCreateOwner_BadRequest() throws Exception {

        OwnerDTO invalidOwnerTO = OwnerDTO.builder().firstName("Sin").city("Lima").build();   // sin lastName

        mockMvc.perform(post("/owners")
                        .content(om.writeValueAsString(invalidOwnerTO))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito.verify(ownerService, Mockito.never()).create(any(OwnerDTO.class));
    }

    @Test
    public void testFindByLastName() throws Exception {

        Mockito.when(ownerService.findByLastName("Davis"))
                .thenReturn(OwnerTObjectCreator.getOwnersDavisTO());

        mockMvc.perform(get("/owners").param("lastName", "Davis"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[0].lastName", is("Davis")))
                .andExpect(jsonPath("$[1].lastName", is("Davis")));
    }

    // ---------------- Actualización y borrado ----------------

    @Test
    public void testUpdateOwner() throws Exception {

        OwnerDTO existingOwnerTO = OwnerTObjectCreator.newOwnerCreatedTO();   // id = 11
        Integer id = existingOwnerTO.getId();

        Mockito.when(ownerService.findById(id)).thenReturn(existingOwnerTO);
        Mockito.when(ownerService.update(any(OwnerDTO.class))).thenAnswer(inv -> inv.getArgument(0));

        OwnerDTO upOwnerTO = OwnerDTO.builder()
                .id(id).firstName("Alexander").lastName("Ramos Vega")
                .address("Av. Ejercito 456").city("Cusco").telephone("922222222")
                .build();

        // UPDATE
        mockMvc.perform(put("/owners/" + id)
                        .content(om.writeValueAsString(upOwnerTO))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName", is("Ramos Vega")));

        // FIND
        mockMvc.perform(get("/owners/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName", is("Ramos Vega")))
                .andExpect(jsonPath("$.city", is("Cusco")));
    }

    @Test
    public void testDeleteOwner() throws Exception {

        Integer id = OwnerTObjectCreator.newOwnerCreatedTO().getId();

        Mockito.doNothing().when(ownerService).delete(id);

        mockMvc.perform(delete("/owners/" + id))
                .andExpect(status().isOk());

        Mockito.verify(ownerService).delete(id);
    }

    @Test
    public void testDeleteOwnerKO() throws Exception {

        Mockito.doThrow(new OwnerNotFoundException("Record not found...!"))
                .when(ownerService).delete(1000);

        mockMvc.perform(delete("/owners/1000"))
                .andExpect(status().isNotFound());
    }
}