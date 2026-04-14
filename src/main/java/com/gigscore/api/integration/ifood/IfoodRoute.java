package com.gigscore.api.integration.ifood;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.net.ConnectException;

@Component
public class IfoodRoute extends RouteBuilder {

    @Override
    public void configure() {

        onException(ConnectException.class, org.apache.camel.http.base.HttpOperationFailedException.class)
            .handled(true)
            .maximumRedeliveries(3) // Tenta novamente 3 vezes
            .redeliveryDelay(2000)  // Espera 2 segundos entre as tentativas
            .backOffMultiplier(2)   // Exponencial (2s, 4s, 8s)
            .logRetryAttempted(true)
            .log("⚠️ Falha ao acessar API do iFood. Tentativa ${header.CamelRedeliveryCounter} de 3 para o usuário: ${header.userId}");


        from("direct:buscarDadosIfood")
            .routeId("rota-ifood-summary")
            .log("Buscando dados do iFood para o usuário: ${header.userId}")
            .toD("http://localhost:8086/ifood/summary/${header.userId}")
            .log("✅ Retorno da API do iFood: ${body}");

        from("direct:buscarPerformanceIfood")
            .routeId("rota-ifood-perfomance")
            .log("Buscando dados de performance do iFood para o usuário: ${header.userId}")
            .toD("http://localhost:8086/ifood/performance/${header.userId}")
            .log("✅ Retorno da API do iFood: ${body}");

        from("direct:BuscarGanhosIfood")
            .routeId("rota-ifood-ganhos")
            .log("Buscando dados de ganhos do iFood para o usuário: ${header.userId}")
            .toD("http://localhost:8086/ifood/earnings/${header.userId}")
            .log("✅ Retorno da API do iFood: ${body}");
    }
}
