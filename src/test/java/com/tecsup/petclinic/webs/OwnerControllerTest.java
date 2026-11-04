package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tecsup.petclinic.dtos.OwnerDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración de OwnerController (controlador -> servicio -> repositorio -> H2).
 * @Transactional: cada prueba hace rollback y los datos de data.sql quedan intactos.
 */
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@Slf4j
public class OwnerControllerTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    // ---------------- Listado y consulta ----------------

    @Test
    public void testFindAllOwners() throws Exception {

        final int NRO_RECORDS = 10;
        final int ID_FIRST_RECORD = 1;

        this.mockMvc.perform(get("/owners"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(NRO_RECORDS)))
                .andExpect(jsonPath("$[0].id", is(ID_FIRST_RECORD)));
    }

    @Test
    public void testFindOwnerOK() throws Exception {

        this.mockMvc.perform(get("/owners/1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("George")))
                .andExpect(jsonPath("$.lastName", is("Franklin")))
                .andExpect(jsonPath("$.city", is("Madison")));
    }

    @Test
    public void testFindOwnerKO() throws Exception {

        mockMvc.perform(get("/owners/666"))
                .andExpect(status().isNotFound());
    }

    // ---------------- Creación ----------------

    @Test
    public void testCreateOwner() throws Exception {

        OwnerDTO newOwnerTO = OwnerDTO.builder()
                .firstName("Alexander")
                .lastName("Ramos")
                .address("Av. Cascanueces 2221")
                .city("Lima")
                .telephone("987654321")
                .build();

        this.mockMvc.perform(post("/owners")
                        .content(om.writeValueAsString(newOwnerTO))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is("Alexander")))
                .andExpect(jsonPath("$.lastName", is("Ramos")))
                .andExpect(jsonPath("$.address", is("Av. Cascanueces 2221")))
                .andExpect(jsonPath("$.city", is("Lima")))
                .andExpect(jsonPath("$.telephone", is("987654321")));
    }

    @Test
    public void testCreateOwner_BadRequest() throws Exception {

        OwnerDTO invalidOwnerTO = OwnerDTO.builder()      // sin lastName
                .firstName("Sin")
                .city("Lima")
                .build();

        this.mockMvc.perform(post("/owners")
                        .content(om.writeValueAsString(invalidOwnerTO))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testFindByLastName() throws Exception {

        this.mockMvc.perform(get("/owners").param("lastName", "Davis"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[0].lastName", is("Davis")))
                .andExpect(jsonPath("$[1].lastName", is("Davis")));
    }

    // ---------------- Actualización y borrado ----------------

    @Test
    public void testUpdateOwner() throws Exception {

        OwnerDTO newOwnerTO = OwnerDTO.builder()
                .firstName("Lucia").lastName("Torres")
                .address("Jr. Lima 123").city("Arequipa").telephone("911111111")
                .build();

        // CREATE
        ResultActions mvcActions = mockMvc.perform(post("/owners")
                        .content(om.writeValueAsString(newOwnerTO))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        String response = mvcActions.andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.parse(response).read("$.id");

        // UPDATE
        OwnerDTO upOwnerTO = OwnerDTO.builder()
                .id(id)
                .firstName("Lucia Maria").lastName("Torres Vega")
                .address("Av. Ejercito 456").city("Cusco").telephone("922222222")
                .build();

        mockMvc.perform(put("/owners/" + id)
                        .content(om.writeValueAsString(upOwnerTO))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // FIND
        mockMvc.perform(get("/owners/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.firstName", is("Lucia Maria")))
                .andExpect(jsonPath("$.lastName", is("Torres Vega")))
                .andExpect(jsonPath("$.city", is("Cusco")));
    }

    @Test
    public void testDeleteOwner() throws Exception {

        OwnerDTO newOwnerTO = OwnerDTO.builder()
                .firstName("Mario").lastName("Quispe")
                .address("Calle Sol 789").city("Trujillo").telephone("933333333")
                .build();

        // CREATE
        ResultActions mvcActions = mockMvc.perform(post("/owners")
                        .content(om.writeValueAsString(newOwnerTO))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        String response = mvcActions.andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.parse(response).read("$.id");

        // DELETE
        mockMvc.perform(delete("/owners/" + id))
                .andExpect(status().isOk());

        // VALIDATE
        mockMvc.perform(get("/owners/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteOwnerKO() throws Exception {

        mockMvc.perform(delete("/owners/1000"))
                .andExpect(status().isNotFound());
    }
}