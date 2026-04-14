package com.gigscore.api.controller;

import com.gigscore.api.service.IfoodService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/ifood", produces = MediaType.APPLICATION_JSON_VALUE)
public class IfoodController {

    private final IfoodService ifoodService;

    public IfoodController(IfoodService ifoodService) {
        this.ifoodService = ifoodService;
    }

    @GetMapping("/resumo/{userId}")
    public String buscarResumoIfood(@PathVariable String userId) {
        return ifoodService.buscarDados(userId);
    }

    @GetMapping("/perfomance/{userId}")
    public String buscarPerformanceIfood(@PathVariable String userId) {
        return ifoodService.buscarPerfomance(userId);
    }

    @GetMapping("/earnings/{userId}")
    public String buscarGanhosIfood(@PathVariable String userId) {
        return ifoodService.buscarGanhos(userId);
    }


}
