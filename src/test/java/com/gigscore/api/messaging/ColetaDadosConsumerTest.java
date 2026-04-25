package com.gigscore.api.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigscore.api.service.ContasService;
import com.gigscore.api.service.IfoodService;
import com.gigscore.api.service.UberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ColetaDadosConsumerTest {

    @Mock
    private IfoodService ifoodService;

    @Mock
    private UberService uberService;

    @Mock
    private ContasService contasService;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private ColetaDadosConsumer consumer;

    @Test
    void escutarSolicitacao_DeveAcionarServicosParaColetarDados() {
        String mensagemJson = "{\"analysisId\": \"123\", \"cpf\": \"12345678900\"}";
        String cpfExtraido = "12345678900";

        when(ifoodService.buscarTodosOsDados(cpfExtraido)).thenReturn("{\"status\": \"sucesso\"}");
        when(uberService.buscarTodosOsDados(cpfExtraido)).thenReturn("{\"status\": \"sucesso\"}");
        when(contasService.buscarTodosOsDados(cpfExtraido)).thenReturn("{\"status\": \"sucesso\"}");

        consumer.escutarSolicitacao(mensagemJson);

        verify(ifoodService, times(1)).buscarTodosOsDados(cpfExtraido);
        verify(uberService, times(1)).buscarTodosOsDados(cpfExtraido);
        verify(contasService, times(1)).buscarTodosOsDados(cpfExtraido);
        verify(kafkaTemplate, times(1)).send(eq("financial-data-collected"), eq("123"), anyString());
    }
}
