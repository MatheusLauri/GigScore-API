package com.gigscore.api.controller;

import com.gigscore.api.service.UberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UberController.class)
class UberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UberService uberService;

    @Test
    void buscarResumoUber_DeveRetornarSucesso() throws Exception {
        String userId = "456";
        String mockResponse = "{\"status\": \"sucesso\"}";

        when(uberService.buscarDados(userId)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/uber/resumo/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(mockResponse));
    }
}
