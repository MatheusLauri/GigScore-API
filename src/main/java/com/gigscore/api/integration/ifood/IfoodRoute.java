package com.gigscore.api.integration.ifood;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.net.ConnectException;

@Component
public class IfoodRoute extends RouteBuilder {

    @Override
    public void configure() {

        // =========================================================
        // RESILIÊNCIA (RETRY GLOBAL)
        // =========================================================
        onException(ConnectException.class, org.apache.camel.http.base.HttpOperationFailedException.class)
            .handled(true)
            .maximumRedeliveries(3) 
            .redeliveryDelay(2000)  
            .backOffMultiplier(2)   
            .logRetryAttempted(true)
            .log("⚠️ Falha ao acessar API do iFood. Tentativa ${header.CamelRedeliveryCounter} de 3 para o usuário: ${header.userId}");

        // =========================================================
        // ORQUESTRADOR DE AGREGAÇÃO (I-FOOD COMPLETO)
        // =========================================================
        from("direct:buscarTodosDadosIfood")
            .routeId("rota-ifood-orquestrador")
            .log("🚀 Iniciando coleta agregada e paralela do iFood para o CPF: ${header.userId}")
            
            // O multicast chama todas as três rotas ao mesmo tempo e junta os três JSONs usando nossa estratégia
            .multicast(new IfoodAggregationStrategy())
                .parallelProcessing()
                .timeout(5000) // Se uma das três demorar mais de 5s, ela é abortada para não travar o processo
                .to(
                    "direct:buscarDadosIfood",
                    "direct:buscarPerformanceIfood",
                    "direct:BuscarGanhosIfood"
                )
            .end() // Fecha o bloco multicast
            
            .log("📦 Dados do iFood 100% agregados: ${body}");


        // =========================================================
        // CHAMADAS INDIVIDUAIS (MOCKS)
        // =========================================================

        from("direct:buscarDadosIfood")
            .routeId("rota-ifood-summary")
            .log("Buscando dados (Summary) do iFood para o usuário: ${header.userId}")
            .toD("http://localhost:8086/ifood/summary/${header.userId}");

        from("direct:buscarPerformanceIfood")
            .routeId("rota-ifood-perfomance")
            .log("Buscando dados de Performance do iFood para o usuário: ${header.userId}")
            .toD("http://localhost:8086/ifood/performance/${header.userId}");

        from("direct:BuscarGanhosIfood")
            .routeId("rota-ifood-ganhos")
            .log("Buscando dados de Ganhos do iFood para o usuário: ${header.userId}")
            .toD("http://localhost:8086/ifood/earnings/${header.userId}");
    }
}
