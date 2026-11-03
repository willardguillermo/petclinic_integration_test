package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tecsup.petclinic.dtos.VetDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración de VetController
 * (controlador -> servicio -> repositorio -> H2, sin mocks)
 */
@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class VetControllerTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    /**
     * GET /vets -> 200 OK y lista JSON
     */
    @Test
    public void testFindAllVets() throws Exception {

        final int ID_FIRST_RECORD = 1;
        final String LAST_NAME_FIRST_RECORD = "Carter";

        this.mockMvc.perform(get("/vets"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[0].id", is(ID_FIRST_RECORD)))
                .andExpect(jsonPath("$[0].lastName", is(LAST_NAME_FIRST_RECORD)));
    }

    /**
     * GET /vets/1 -> 200 OK, James Carter
     */
    @Test
    public void testFindVetOK() throws Exception {

        String FIRST_NAME = "James";
        String LAST_NAME = "Carter";
        String EMAIL = "james.carter@petclinic.com";

        this.mockMvc.perform(get("/vets/1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is(FIRST_NAME)))
                .andExpect(jsonPath("$.lastName", is(LAST_NAME)))
                .andExpect(jsonPath("$.email", is(EMAIL)))
                .andExpect(jsonPath("$.active", is(true)));
    }

    /**
     * GET /vets/666 -> 404 Not Found
     */
    @Test
    public void testFindVetKO() throws Exception {

        mockMvc.perform(get("/vets/666"))
                .andExpect(status().isNotFound());
    }

    /**
     * POST /vets -> 201 Created
     */
    @Test
    public void testCreateVet() throws Exception {

        String FIRST_NAME = "Gregory";
        String LAST_NAME = "House";
        String EMAIL = "gregory.house@petclinic.com";
        String PHONE = "987654321";

        VetDTO newVetDTO = VetDTO.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .phone(PHONE)
                .build();

        this.mockMvc.perform(post("/vets")
                        .content(om.writeValueAsString(newVetDTO))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is(FIRST_NAME)))
                .andExpect(jsonPath("$.lastName", is(LAST_NAME)))
                .andExpect(jsonPath("$.email", is(EMAIL)))
                .andExpect(jsonPath("$.phone", is(PHONE)))
                .andExpect(jsonPath("$.active", is(true)));
    }

    /**
     * POST + PUT /vets/{id} -> 200 OK y GET confirma el cambio
     */
    @Test
    public void testUpdateVet() throws Exception {

        String FIRST_NAME = "Lisa";
        String LAST_NAME = "Cuddy";
        String EMAIL = "lisa.cuddy@petclinic.com";
        String PHONE = "911111111";

        String UP_FIRST_NAME = "Lisa Marie";
        String UP_LAST_NAME = "Cuddy Smith";
        String UP_EMAIL = "lisa.smith@petclinic.com";
        String UP_PHONE = "922222222";

        VetDTO newVetDTO = VetDTO.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .phone(PHONE)
                .build();

        // CREATE
        ResultActions mvcActions = mockMvc.perform(post("/vets")
                        .content(om.writeValueAsString(newVetDTO))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        String response = mvcActions.andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.parse(response).read("$.id");

        // UPDATE
        VetDTO upVetDTO = VetDTO.builder()
                .id(id)
                .firstName(UP_FIRST_NAME)
                .lastName(UP_LAST_NAME)
                .email(UP_EMAIL)
                .phone(UP_PHONE)
                .build();

        mockMvc.perform(put("/vets/" + id)
                        .content(om.writeValueAsString(upVetDTO))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // FIND
        mockMvc.perform(get("/vets/" + id))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.firstName", is(UP_FIRST_NAME)))
                .andExpect(jsonPath("$.lastName", is(UP_LAST_NAME)))
                .andExpect(jsonPath("$.email", is(UP_EMAIL)))
                .andExpect(jsonPath("$.phone", is(UP_PHONE)));

        // DELETE (limpieza)
        mockMvc.perform(delete("/vets/" + id))
                .andExpect(status().isOk());
    }

    /**
     * POST + DELETE /vets/{id} -> 200 OK y luego GET -> 404
     */
    @Test
    public void testDeleteVet() throws Exception {

        VetDTO newVetDTO = VetDTO.builder()
                .firstName("James")
                .lastName("Wilson")
                .email("james.wilson@petclinic.com")
                .phone("933333333")
                .build();

        ResultActions mvcActions = mockMvc.perform(post("/vets")
                        .content(om.writeValueAsString(newVetDTO))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        String response = mvcActions.andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.parse(response).read("$.id");

        mockMvc.perform(delete("/vets/" + id))
                .andExpect(status().isOk());

        // Validación: ya no existe
        mockMvc.perform(get("/vets/" + id))
                .andExpect(status().isNotFound());
    }

    /**
     * DELETE /vets/1000 -> 404 Not Found
     */
    @Test
    public void testDeleteVetKO() throws Exception {

        mockMvc.perform(delete("/vets/" + "1000"))
                .andExpect(status().isNotFound());
    }
}