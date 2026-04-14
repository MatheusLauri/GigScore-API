package com.gigscore.api.service;

import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Service;

@Service
public class UberService {
    private final ProducerTemplate producerTemplate;

    public UberService(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    public String buscarDados(String userId) {
        return producerTemplate.requestBodyAndHeader(
                "direct:BuscarDadosUber",
                null,
                "userId",
                userId,
                String.class
        );
    }

    public String buscarPerfomance(String userId) {
        return producerTemplate.requestBodyAndHeader(
                "direct:BuscarPerformanceUber",
                null,
                "userId",
                userId,
                String.class
        );
    }

    public String buscarGanhos(String userId) {
        return producerTemplate.requestBodyAndHeader(
                "direct:BuscarGanhosUber",
                null,
                "userId",
                userId,
                String.class
        );
    }
}
