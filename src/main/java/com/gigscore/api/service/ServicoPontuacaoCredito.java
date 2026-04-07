package com.gigscore.api.service;

import com.gigscore.api.dto.RespostaAnaliseCredito;
import com.gigscore.api.dto.SolicitacaoAnaliseCredito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ServicoPontuacaoCredito {

    private final int limiteAprovacao;

    public ServicoPontuacaoCredito(@Value("${analise.credito.limite-aprovacao:600}") int limiteAprovacao) {
        this.limiteAprovacao = limiteAprovacao;
    }

    public RespostaAnaliseCredito calcularPontuacao(SolicitacaoAnaliseCredito solicitacao) {
        int base = pontuacaoBasePorRenda(solicitacao.rendaMensal());
        int comAjuste = aplicarAjustePeloTempoNaGig(base, solicitacao.mesesNaGigEconomia());
        int pontuacao = limitarEntre(comAjuste, 0, 1000);
        boolean aprovado = pontuacao >= limiteAprovacao;
        return new RespostaAnaliseCredito(pontuacao, aprovado);
    }

    private static int pontuacaoBasePorRenda(BigDecimal rendaMensal) {
        if (rendaMensal.compareTo(new BigDecimal("2000")) < 0) {
            return 350;
        }
        if (rendaMensal.compareTo(new BigDecimal("4000")) < 0) {
            return 550;
        }
        if (rendaMensal.compareTo(new BigDecimal("8000")) < 0) {
            return 700;
        }
        return 820;
    }

    private static int aplicarAjustePeloTempoNaGig(int base, Integer mesesNaGigEconomia) {
        if (mesesNaGigEconomia == null) {
            return base;
        }
        if (mesesNaGigEconomia >= 12) {
            return base + 40;
        }
        if (mesesNaGigEconomia < 3) {
            return base - 30;
        }
        return base;
    }

    private static int limitarEntre(int valor, int minimo, int maximo) {
        return Math.min(maximo, Math.max(minimo, valor));
    }
}
