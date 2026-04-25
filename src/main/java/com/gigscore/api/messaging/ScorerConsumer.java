package com.gigscore.api.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigscore.api.dto.CreditAnalysisResponse;
import com.gigscore.api.dto.NormalizedFinancialData;
import com.gigscore.api.service.DatabaseService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ScorerConsumer {

    private final ObjectMapper objectMapper;
    private final DatabaseService databaseService;

    public ScorerConsumer(ObjectMapper objectMapper, DatabaseService databaseService) {
        this.objectMapper = objectMapper;
        this.databaseService = databaseService;
    }

    @KafkaListener(topics = "normalized-data-ready", groupId = "gigscore-scorer-group")
    public void calcularScore(String dadosNormalizadosJson) {
        System.out.println("📥 Mensagem recebida no Kafka (normalized-data-ready): Iniciando Motor de Regras...");

        try {
            // Converte o JSON string para o nosso DTO padronizado manualmente com Jackson
            NormalizedFinancialData dados = objectMapper.readValue(dadosNormalizadosJson, NormalizedFinancialData.class);
            String analysisId = dados.analysisId();

            System.out.println("🧮 Calculando pontuação para a análise ID: " + analysisId);

            // --- LÓGICA DE NEGÓCIO (SCORER) ---
            BigDecimal rendaTotal = dados.totalMonthlyIncome();
            int scoreBancario = dados.paymentHistoryScore();

            // 1. Calcula o Score (0 a 1000)
            int scoreFinal = 300; // Base
            if (rendaTotal.compareTo(new BigDecimal("3000")) > 0) scoreFinal += 200;
            if (rendaTotal.compareTo(new BigDecimal("5000")) > 0) scoreFinal += 200;
            scoreFinal += (scoreBancario / 2);
            scoreFinal = Math.min(scoreFinal, 1000);

            // 2. Regra de Aprovação
            boolean aprovado = scoreFinal >= 600;
            
            // 3. Calcula o Limite
            int limite = aprovado ? rendaTotal.multiply(new BigDecimal("0.4")).intValue() : 0;
            
            // 4. Gera a explicação
            String explicacao = aprovado 
                ? "Crédito aprovado! Renda consistente na Gig Economy comprovada." 
                : "Crédito negado. Score não atingiu a pontuação mínima exigida (600).";

            // --- ATUALIZAÇÃO DO BANCO DE DADOS ---
            CreditAnalysisResponse resultadoFinal = new CreditAnalysisResponse(
                    analysisId,
                    "COMPLETED",
                    scoreFinal,
                    aprovado,
                    limite,
                    explicacao
            );

            databaseService.save(resultadoFinal);
            System.out.println("🏆 CICLO FECHADO! Análise " + analysisId + " finalizada. Status atualizado no banco para COMPLETED.");

        } catch (Exception e) {
            System.err.println("❌ Erro ao calcular o score ou atualizar o banco: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
