package com.gigscore.api.service;

import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Service;

@Service
public class IfoodService {

    private final ProducerTemplate producerTemplate;

    public IfoodService(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    public String buscarTodosOsDados(String userId) {
        // Dispara a rota orquestradora do iFood que faz o multicast e agregação
        return producerTemplate.requestBodyAndHeader(
                "direct:buscarTodosDadosIfood",
                null,
                "userId",
                userId,
                String.class
        );
    }

    // Mantive as rotas individuais caso precise delas para os testes ou controllers antigos
    public String buscarDados(String userId) {
        return producerTemplate.requestBodyAndHeader(
                "direct:buscarDadosIfood",
                null,
                "userId",
                userId,
                String.class
        );
    }

    public String buscarPerfomance(String userId) {
        return producerTemplate.requestBodyAndHeader(
                "direct:buscarPerformanceIfood",
                null,
                "userId",
                userId,
                String.class
        );
    }

    public String buscarGanhos(String userId) {
        return producerTemplate.requestBodyAndHeader(
                "direct:BuscarGanhosIfood",
                null,
                "userId",
                userId,
                String.class
        );
    }
}
