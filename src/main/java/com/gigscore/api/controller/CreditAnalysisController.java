package com.gigscore.api.controller;

import com.gigscore.api.dto.CreditAnalysisRequest;
import com.gigscore.api.dto.CreditAnalysisResponse;
import com.gigscore.api.dto.CreditAnalysisStatusResponse;
import com.gigscore.api.messaging.AnaliseCreditoProducer;
import com.gigscore.api.service.DatabaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/credit-analysis", produces = MediaType.APPLICATION_JSON_VALUE)
public class CreditAnalysisController {

    private final AnaliseCreditoProducer producer;
    private final DatabaseService databaseService;

    public CreditAnalysisController(AnaliseCreditoProducer producer, DatabaseService databaseService) {
        this.producer = producer;
        this.databaseService = databaseService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreditAnalysisStatusResponse> iniciarAnalise(@Valid @RequestBody CreditAnalysisRequest request) {
        String analysisId = UUID.randomUUID().toString();

        CreditAnalysisResponse processando = new CreditAnalysisResponse(
                analysisId, "PROCESSING", null, null, null, null
        );
        databaseService.save(processando);

        producer.enviarSolicitacao(analysisId, request.cpf());

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new CreditAnalysisStatusResponse(analysisId, "PROCESSING"));
    }

    @GetMapping("/{analysisId}")
    public ResponseEntity<CreditAnalysisResponse> consultarStatus(@PathVariable String analysisId) {
        CreditAnalysisResponse analise = databaseService.findById(analysisId);

        if (analise == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(analise);
    }

    @GetMapping
    public ResponseEntity<List<CreditAnalysisResponse>> listarAnalises() {
        return ResponseEntity.ok(databaseService.findAll());
    }

    @PostMapping("/{analysisId}/retry")
    public ResponseEntity<CreditAnalysisStatusResponse> reprocessarAnalise(@PathVariable String analysisId) {
        CreditAnalysisResponse analise = databaseService.findById(analysisId);
        
        if (analise == null) {
            return ResponseEntity.notFound().build();
        }

        databaseService.save(new CreditAnalysisResponse(analysisId, "PROCESSING", null, null, null, null));
        
        producer.enviarSolicitacao(analysisId, "00000000000"); // Mockado por enquanto
        
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new CreditAnalysisStatusResponse(analysisId, "PROCESSING"));
    }

    @GetMapping("/{analysisId}/details")
    public ResponseEntity<String> consultarDetalhes(@PathVariable String analysisId) {
        if (databaseService.findById(analysisId) == null) {
            return ResponseEntity.notFound().build();
        }

        String mockDetails = """
        {
          "income": 1200,
          "incomeStability": 0.8,
          "paymentDiscipline": 0.6,
          "sources": {
            "uber": "OK",
            "ifood": "TIMEOUT",
            "bills": "OK"
          }
        }
        """;
        return ResponseEntity.ok(mockDetails);
    }
}
