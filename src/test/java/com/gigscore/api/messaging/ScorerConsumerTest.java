package com.gigscore.api.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigscore.api.dto.CreditAnalysisResponse;
import com.gigscore.api.service.DatabaseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ScorerConsumerTest {

    @Mock
    private DatabaseService databaseService;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private ScorerConsumer scorerConsumer;

    @Test
    void calcularScore_DeveCalcularEAtualizarBanco() {
        String pacoteFinanceiro = """
        {
          "analysisId": "test-id-123",
          "cpf": "12345678900",
          "ifood": { "ganhos_totais": 3000.00 },
          "uber": { "ganhos_totais": 2500.00 },
          "contas": { "score_pagamento": 800 }
        }
        """;

        scorerConsumer.calcularScore(pacoteFinanceiro);

        // Captura o objeto que foi enviado para o método save() do nosso banco de dados mock
        ArgumentCaptor<CreditAnalysisResponse> captor = ArgumentCaptor.forClass(CreditAnalysisResponse.class);
        verify(databaseService).save(captor.capture());

        CreditAnalysisResponse resultadoFinal = captor.getValue();

        assertEquals("test-id-123", resultadoFinal.analysisId());
        assertEquals("COMPLETED", resultadoFinal.status());
        assertTrue(resultadoFinal.score() > 800); // 300 + 200 + 200 + (800/2) = 1100, limitado a 1000
        assertEquals(1000, resultadoFinal.score());
        assertTrue(resultadoFinal.approved());
    }
}
