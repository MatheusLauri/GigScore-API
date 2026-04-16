package com.gigscore.api.integration.uber;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.net.ConnectException;

@Component
public class UberRoute extends RouteBuilder {

    @Override
    public void configure() {

        onException(ConnectException.class, org.apache.camel.http.base.HttpOperationFailedException.class)
                .handled(true)
                .maximumRedeliveries(3) // Tenta novamente 3 vezes
                .redeliveryDelay(2000)  // Espera 2 segundos entre as tentativas
                .backOffMultiplier(2)   // Exponencial (2s, 4s, 8s)
                .logRetryAttempted(true)
                .log("⚠️ Falha ao acessar API do Uber. Tentativa ${header.CamelRedeliveryCounter} de 3 para o usuário: ${header.userId}");


        from("direct:buscarTodosDadosUber")
            .routeId("rota-uber-orquestrador")
            .log("🚀 Iniciando coleta agregada e paralela da Uber para o CPF: ${header.userId}")
            .multicast(new UberAggregationStrategy())
                .parallelProcessing()
                .timeout(5000)
                .to(
                    "direct:BuscarDadosUber",
                    "direct:BuscarPerformanceUber",
                    "direct:BuscarGanhosUber"
                )
            .end()
            .log("📦 Dados da Uber 100% agregados: ${body}");


        from("direct:BuscarDadosUber")
                .routeId("rota-uber-dados")
                .log("Buscando dados do uber para o usuário: ${header.userId}")
                .toD("http://localhost:8084/uber/summary/${header.userId}");

        from("direct:BuscarGanhosUber")
                .routeId("rota-uber-ganhos")
                .log("Buscando dados de ganhos do Uber para o usuário: ${header.userId}")
                .toD("http://localhost:8084/uber/earnings/${header.userId}");

        from("direct:BuscarPerformanceUber")
                .routeId("rota-uber-performance")
                .log("Buscando dados de performance do Uber para o usuário: ${header.userId}")
                .toD("http://localhost:8084/uber/performance/${header.userId}");
    }
}
