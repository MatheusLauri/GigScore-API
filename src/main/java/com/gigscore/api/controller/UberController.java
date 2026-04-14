package com.gigscore.api.controller;

import com.gigscore.api.service.UberService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/uber", produces = MediaType.APPLICATION_JSON_VALUE)
public class UberController {

    private final UberService uberService;

    public UberController(UberService uberService) {
        this.uberService = uberService;
    }

    @GetMapping("/resumo/{userId}")
    public String buscarResumoUber(@PathVariable String userId) {
        return uberService.buscarDados(userId);
    }

    @GetMapping("/perfomance/{userId}")
    public String buscarPerformanceUber(@PathVariable String userId) {
        return uberService.buscarPerfomance(userId);
    }

    @GetMapping("/earnings/{userId}")
    public String buscarGanhosUber(@PathVariable String userId) {
        return uberService.buscarGanhos(userId);
    }

}
