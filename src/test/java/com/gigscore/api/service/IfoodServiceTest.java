package com.gigscore.api.service;

import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.BeforeEach;
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
class IfoodServiceTest {

    @Mock
    private ProducerTemplate producerTemplate;

    @InjectMocks
    private IfoodService ifoodService;

    private final String userId = "123";

    @Test
    void buscarDados_DeveChamarRotaCorreta() {
        String expectedResponse = "{\"status\": \"sucesso\"}";
        when(producerTemplate.requestBodyAndHeader("direct:buscarDadosIfood", null, "userId", userId, String.class))
                .thenReturn(expectedResponse);

        String response = ifoodService.buscarDados(userId);

        assertEquals(expectedResponse, response);
        verify(producerTemplate).requestBodyAndHeader(eq("direct:buscarDadosIfood"), eq(null), eq("userId"), eq(userId), eq(String.class));
    }

    @Test
    void buscarPerformance_DeveChamarRotaCorreta() {
        String expectedResponse = "{\"status\": \"sucesso\"}";
        when(producerTemplate.requestBodyAndHeader("direct:buscarPerformanceIfood", null, "userId", userId, String.class))
                .thenReturn(expectedResponse);

        String response = ifoodService.buscarPerfomance(userId);

        assertEquals(expectedResponse, response);
        verify(producerTemplate).requestBodyAndHeader(eq("direct:buscarPerformanceIfood"), eq(null), eq("userId"), eq(userId), eq(String.class));
    }

    @Test
    void buscarGanhos_DeveChamarRotaCorreta() {
        String expectedResponse = "{\"status\": \"sucesso\"}";
        when(producerTemplate.requestBodyAndHeader("direct:BuscarGanhosIfood", null, "userId", userId, String.class))
                .thenReturn(expectedResponse);

        String response = ifoodService.buscarGanhos(userId);

        assertEquals(expectedResponse, response);
        verify(producerTemplate).requestBodyAndHeader(eq("direct:BuscarGanhosIfood"), eq(null), eq("userId"), eq(userId), eq(String.class));
    }
}
