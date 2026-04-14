package com.gigscore.api.messaging;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AnaliseCreditoProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPICO = "credit-analysis-requested";

    public AnaliseCreditoProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void enviarSolicitacao(String cpf) {
        // Envia o CPF para o tópico do Kafka
        kafkaTemplate.send(TOPICO, cpf, "{\"cpf\": \"" + cpf + "\"}");
        System.out.println("✅ Solicitação enviada para o Kafka: CPF " + cpf);
    }
}
