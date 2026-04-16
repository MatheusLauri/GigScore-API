package com.gigscore.api.integration.uber;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

public class UberAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            return newExchange;
        }

        String dadosAgregados = oldExchange.getIn().getBody(String.class);
        String novoDado = newExchange.getIn().getBody(String.class);

        if (novoDado == null || novoDado.isBlank() || novoDado.startsWith("[")) {
            return oldExchange;
        }
        
        if (dadosAgregados == null || dadosAgregados.isBlank() || dadosAgregados.startsWith("[")) {
            oldExchange.getIn().setBody(novoDado);
            return oldExchange;
        }

        String jsonAgregadoLimpo = dadosAgregados.trim().replaceAll("^\\{|}$", "");
        String jsonNovoLimpo = novoDado.trim().replaceAll("^\\{|}$", "");

        String resultadoFinal = "{" + jsonAgregadoLimpo + ", " + jsonNovoLimpo + "}";

        oldExchange.getIn().setBody(resultadoFinal);
        return oldExchange;
    }
}
