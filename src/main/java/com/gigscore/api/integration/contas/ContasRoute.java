package com.gigscore.api.integration.contas;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.net.ConnectException;

@Component
public class ContasRoute extends RouteBuilder {

    @Override
    public void configure() {

        onException(ConnectException.class, org.apache.camel.http.base.HttpOperationFailedException.class)
            .handled(true)
            .maximumRedeliveries(3) // Tenta novamente 3 vezes
            .redeliveryDelay(2000)  // Espera 2 segundos entre as tentativas
            .backOffMultiplier(2)   // Exponencial (2s, 4s, 8s)
            .logRetryAttempted(true)
            .log("⚠️ Falha ao acessar API de Contas Bancárias. Tentativa ${header.CamelRedeliveryCounter} de 3 para o usuário: ${header.userId}");


        from("direct:BuscarHistoricoContas")
            .routeId("rota-contas-summary")
            .log("Buscando dados de histórico de Contas do usuário: ${header.userId}")
            .toD("http://localhost:8083/bills/history/${header.userId}")
            .log("✅ Retorno da API de Contas (Summary): ${body}");

        from("direct:BuscarScoreDePagamento")
            .routeId("rota-contas-score")
            .log("Buscando dados de score de pagamento para o usuário: ${header.userId}")
            .toD("http://localhost:8083/bills/score/${header.userId}")
            .log("✅ Retorno da API de Contas (Score): ${body}");

        from("direct:BuscarContasPendentes")
            .routeId("rota-contas-pendentes")
            .log("Buscando dados de contas atrasadas para o usuário: ${header.userId}")
            .toD("http://localhost:8083/bills/open/${header.userId}")
            .log("✅ Retorno da API de Contas (Atrasadas): ${body}");
    }
}
