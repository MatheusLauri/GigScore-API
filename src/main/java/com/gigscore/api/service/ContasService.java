package com.gigscore.api.service;

import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Service;

@Service
public class ContasService {
    private final ProducerTemplate producerTemplate;

    public ContasService(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    public String buscarResumoContas(String userId) {
        return producerTemplate.requestBodyAndHeader(
                "direct:BuscarHistoricoContas",
                null,
                "userId",
                userId,
                String.class
        );
    }

    public String buscarScorePagamento(String userId) {
        return producerTemplate.requestBodyAndHeader(
                "direct:BuscarScoreDePagamento",
                null,
                "userId",
                userId,
                String.class
        );
    }

    public String buscarContasAtrasadas(String userId) {
        return producerTemplate.requestBodyAndHeader(
                "direct:BuscarContasPendentes",
                null,
                "userId",
                userId,
                String.class
        );
    }
}
