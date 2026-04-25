package com.gigscore.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigscore.api.dto.CreditAnalysisRequest;
import com.gigscore.api.messaging.AnaliseCreditoProducer;
import com.gigscore.api.service.DatabaseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CreditAnalysisController.class)
class CreditAnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AnaliseCreditoProducer producer;

    @MockitoBean
    private DatabaseService databaseService;

    @Test
    void iniciarAnalise_DeveRetornarAcceptedEEnviarParaKafka() throws Exception {
        CreditAnalysisRequest request = new CreditAnalysisRequest("user-123", "00000000000");

        mockMvc.perform(post("/credit-analysis")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("PROCESSING"))
                .andExpect(jsonPath("$.analysisId").exists());

        // O Producer agora recebe 2 parâmetros: analysisId e cpf
        verify(producer).enviarSolicitacao(anyString(), eq("00000000000"));
    }

    @Test
    void consultarStatus_QuandoNaoExiste_DeveRetornarNotFound() throws Exception {
        mockMvc.perform(get("/credit-analysis/{analysisId}", "id-inexistente"))
                .andExpect(status().isNotFound());
    }
}
