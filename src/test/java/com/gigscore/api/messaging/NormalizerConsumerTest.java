package com.gigscore.api.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigscore.api.dto.NormalizedFinancialData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NormalizerConsumerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private NormalizerConsumer normalizerConsumer;

    @Test
    void normalize_DeveGerarEPublicarDadosNormalizados() throws Exception {
        String pacoteFinanceiroBruto = """
        {
          "analysisId": "123",
          "cpf": "000",
          "ifood": { "ganhos_totais": 2000.00, "avaliacao": 4.8, "entregas_concluidas": 100 },
          "uber": { "ganhos_totais": 3000.00, "avaliacao": 4.9, "corridas_concluidas": 150 },
          "contas": { "score_pagamento": 850 }
        }
        """;

        normalizerConsumer.normalize(pacoteFinanceiroBruto);

        // O NormalizerConsumer agora publica uma String (JSON) e não mais um objeto Java!
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq("normalized-data-ready"), eq("123"), captor.capture());

        // Precisamos converter o JSON (String) capturado de volta para objeto para validar os campos no teste
        String jsonPublicado = captor.getValue();
        NormalizedFinancialData dadosLimpos = objectMapper.readValue(jsonPublicado, NormalizedFinancialData.class);

        assertEquals("123", dadosLimpos.analysisId());
        assertEquals("000", dadosLimpos.cpf());
        assertEquals(new BigDecimal("5000.0"), dadosLimpos.totalMonthlyIncome());
        assertEquals(4.85, dadosLimpos.platformReputationScore());
        assertEquals(250, dadosLimpos.totalCompletedGigs());
        assertEquals(850, dadosLimpos.paymentHistoryScore());
    }
}
