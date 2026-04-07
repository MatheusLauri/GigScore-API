package com.gigscore.api.controller;

import com.gigscore.api.dto.RespostaAnaliseCredito;
import com.gigscore.api.dto.SolicitacaoAnaliseCredito;
import com.gigscore.api.service.ServicoPontuacaoCredito;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/analise-credito", produces = MediaType.APPLICATION_JSON_VALUE)
public class ControladorAnaliseCredito {

    private final ServicoPontuacaoCredito servicoPontuacaoCredito;

    public ControladorAnaliseCredito(ServicoPontuacaoCredito servicoPontuacaoCredito) {
        this.servicoPontuacaoCredito = servicoPontuacaoCredito;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public RespostaAnaliseCredito analisar(@Valid @RequestBody SolicitacaoAnaliseCredito solicitacao) {
        return servicoPontuacaoCredito.calcularPontuacao(solicitacao);
    }
}
