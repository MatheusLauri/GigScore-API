package com.gigscore.api.messaging;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AnaliseCreditoProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AnaliseCreditoProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void enviarSolicitacao(String analysisId, String cpf) {
        String payload = String.format("{\"analysisId\": \"%s\", \"cpf\": \"%s\"}", analysisId, cpf);
        kafkaTemplate.send("credit-analysis-requested", cpf, payload);
        System.out.println("✅ Solicitação enviada para o Kafka: " + payload);
    }
}
