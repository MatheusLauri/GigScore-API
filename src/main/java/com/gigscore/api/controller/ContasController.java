package com.gigscore.api.controller;

import com.gigscore.api.service.ContasService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/contas", produces = MediaType.APPLICATION_JSON_VALUE)
public class ContasController {

    private final ContasService contasService;

    public ContasController(ContasService contasService) {
        this.contasService = contasService;
    }

    @GetMapping("/resumo/{userId}")
    public String buscarResumoContas(@PathVariable String userId) {
        return contasService.buscarResumoContas(userId);
    }

    @GetMapping("/score/{userId}")
    public String ScorePagamento(@PathVariable String userId) {
        return contasService.buscarScorePagamento(userId);
    }

    @GetMapping("/atrasadas/{userId}")
    public String buscarContasAtrasadas(@PathVariable String userId) {
        return contasService.buscarContasAtrasadas(userId);
    }
}
