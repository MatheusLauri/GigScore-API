package com.gigscore.api.controller;

import com.gigscore.api.messaging.AnaliseCreditoProducer;
import com.gigscore.api.service.IfoodService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IfoodController.class)
class IfoodControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IfoodService ifoodService;
    
    @MockBean
    private AnaliseCreditoProducer producer;

    @Test
    void solicitarAnalise_DeveEnviarMensagemERetornarSucesso() throws Exception {
        String cpf = "12345678900";
        String expectedResponse = "{\"mensagem\": \"Solicitação de análise enviada para a fila com sucesso!\"}";

        mockMvc.perform(post("/api/ifood/solicitar-analise/{cpf}", cpf))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));

        verify(producer).enviarSolicitacao(cpf);
    }

    @Test
    void buscarResumoIfood_DeveRetornarSucesso() throws Exception {
        String userId = "123";
        String mockResponse = "{\"status\": \"sucesso\"}";

        when(ifoodService.buscarDados(userId)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/ifood/resumo/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(mockResponse));
    }

    @Test
    void buscarPerformanceIfood_DeveRetornarSucesso() throws Exception {
        String userId = "123";
        String mockResponse = "{\"status\": \"sucesso\"}";

        when(ifoodService.buscarPerfomance(userId)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/ifood/perfomance/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(mockResponse));
    }

    @Test
    void buscarGanhosIfood_DeveRetornarSucesso() throws Exception {
        String userId = "123";
        String mockResponse = "{\"status\": \"sucesso\"}";

        when(ifoodService.buscarGanhos(userId)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/ifood/earnings/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(mockResponse));
    }
}
