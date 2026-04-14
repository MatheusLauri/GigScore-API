package com.gigscore.api.controller;

import com.gigscore.api.service.ContasService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContasController.class)
class ContasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContasService contasService;

    @Test
    void buscarResumoContas_DeveRetornarSucesso() throws Exception {
        String userId = "789";
        String mockResponse = "{\"status\": \"sucesso\"}";

        when(contasService.buscarResumoContas(userId)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/contas/resumo/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(mockResponse));
    }
}
