package com.gigscore.api.integration.ifood;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

public class IfoodAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        // Se for a primeira resposta (ex: summary), guarda como a principal (oldExchange)
        if (oldExchange == null) {
            return newExchange;
        }

        // Pega o JSON acumulado até o momento
        String dadosAgregados = oldExchange.getIn().getBody(String.class);
        
        // Pega o JSON da nova requisição que acabou de chegar (ex: performance ou ganhos)
        String novoDado = newExchange.getIn().getBody(String.class);

        // Removemos as chaves dos JSONs ( { e } ) para podermos fundir tudo num objeto só de forma simples
        String jsonAgregadoLimpo = dadosAgregados.trim().replaceAll("^\\{|}$", "");
        String jsonNovoLimpo = novoDado.trim().replaceAll("^\\{|}$", "");

        // Concatena os JSONs (Ex: "status": "sucesso", "entregas": 120, "avaliacao": 4.8)
        String resultadoFinal = "{" + jsonAgregadoLimpo + ", " + jsonNovoLimpo + "}";

        // Atualiza o Body com os dados fundidos
        oldExchange.getIn().setBody(resultadoFinal);
        return oldExchange;
    }
}
