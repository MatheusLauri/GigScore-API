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
class UberServiceTest {

    @Mock
    private ProducerTemplate producerTemplate;

    @InjectMocks
    private UberService uberService;

    private final String userId = "456";

    @Test
    void buscarDados_DeveChamarRotaCorreta() {
        String expectedResponse = "{\"status\": \"sucesso\"}";
        when(producerTemplate.requestBodyAndHeader("direct:BuscarDadosUber", null, "userId", userId, String.class))
                .thenReturn(expectedResponse);

        String response = uberService.buscarDados(userId);

        assertEquals(expectedResponse, response);
        verify(producerTemplate).requestBodyAndHeader(eq("direct:BuscarDadosUber"), eq(null), eq("userId"), eq(userId), eq(String.class));
    }
}
