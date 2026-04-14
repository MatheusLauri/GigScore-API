package com.gigscore.api.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ColetaDadosConsumer {

    @KafkaListener(topics = "credit-analysis-requested", groupId = "gigscore-group")
    public void escutarSolicitacao(String mensagem) {
        System.out.println("📥 Mensagem recebida no Kafka (credit-analysis-requested): " + mensagem);
        // Aqui no futuro o Consumer irá acionar as rotas do Camel para buscar no iFood/Uber/Contas!
    }
}
