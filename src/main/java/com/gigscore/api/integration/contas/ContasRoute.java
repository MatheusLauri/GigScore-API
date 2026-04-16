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

        // =========================================================
        // ORQUESTRADOR DE AGREGAÇÃO (CONTAS COMPLETO)
        // =========================================================
        from("direct:buscarTodosDadosContas")
            .routeId("rota-contas-orquestrador")
            .log("🚀 Iniciando coleta agregada e paralela de Contas para o CPF: ${header.userId}")
            .multicast(new ContasAggregationStrategy())
                .parallelProcessing()
                .timeout(5000)
                .to(
                    "direct:BuscarHistoricoContas",
                    "direct:BuscarScoreDePagamento",
                    "direct:BuscarContasPendentes"
                )
            .end()
            .log("📦 Dados de Contas 100% agregados: ${body}");


        from("direct:BuscarHistoricoContas")
            .routeId("rota-contas-summary")
            .log("Buscando dados de histórico de Contas do usuário: ${header.userId}")
            .toD("http://localhost:8081/contas/summary/${header.userId}");

        from("direct:BuscarScoreDePagamento")
            .routeId("rota-contas-score")
            .log("Buscando dados de score de pagamento para o usuário: ${header.userId}")
            .toD("http://localhost:8081/bills/score/${header.userId}");

        from("direct:BuscarContasPendentes")
            .routeId("rota-contas-pendentes")
            .log("Buscando dados de contas atrasadas para o usuário: ${header.userId}")
            .toD("http://localhost:8081/bills/open/${header.userId}");
    }
}
