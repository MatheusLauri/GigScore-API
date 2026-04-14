package com.gigscore.api.service;

import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContasServiceTest {

    @Mock
    private ProducerTemplate producerTemplate;

    @InjectMocks
    private ContasService contasService;

    private final String userId = "789";

    @Test
    void buscarResumoContas_DeveChamarRotaCorreta() {
        String expectedResponse = "{\"status\": \"sucesso\"}";
        when(producerTemplate.requestBodyAndHeader("direct:BuscarHistoricoContas", null, "userId", userId, String.class))
                .thenReturn(expectedResponse);

        String response = contasService.buscarResumoContas(userId);

        assertEquals(expectedResponse, response);
        verify(producerTemplate).requestBodyAndHeader(eq("direct:BuscarHistoricoContas"), eq(null), eq("userId"), eq(userId), eq(String.class));
    }
}
