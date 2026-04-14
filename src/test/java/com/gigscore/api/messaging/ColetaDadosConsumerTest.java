package com.gigscore.api.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigscore.api.service.IfoodService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ColetaDadosConsumerTest {

    @Mock
    private IfoodService ifoodService;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private ColetaDadosConsumer consumer;

    @Test
    void escutarSolicitacao_DeveAcionarIfoodServiceParaColetarDados() {
        String mensagemJson = "{\"cpf\": \"12345678900\"}";
        String cpfExtraido = "12345678900";

        when(ifoodService.buscarTodosOsDados(cpfExtraido)).thenReturn("{\"status\": \"sucesso\"}");

        consumer.escutarSolicitacao(mensagemJson);

        verify(ifoodService, times(1)).buscarTodosOsDados(cpfExtraido);
    }
}
