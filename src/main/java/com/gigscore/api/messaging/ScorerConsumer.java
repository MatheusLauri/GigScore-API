package com.gigscore.api.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigscore.api.dto.CreditAnalysisResponse;
import com.gigscore.api.service.DatabaseService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ScorerConsumer {

    private final ObjectMapper objectMapper;
    private final DatabaseService databaseService;

    public ScorerConsumer(ObjectMapper objectMapper, DatabaseService databaseService) {
        this.objectMapper = objectMapper;
        this.databaseService = databaseService;
    }

    @KafkaListener(topics = "financial-data-collected", groupId = "gigscore-scorer-group")
    public void calcularScore(String pacoteFinanceiro) {
        System.out.println("📥 Mensagem recebida no Kafka (financial-data-collected): Iniciando Motor de Regras...");

        try {
            // Extrai as informações do pacote gerado pelo Data Collector
            JsonNode jsonNode = objectMapper.readTree(pacoteFinanceiro);
            String analysisId = jsonNode.get("analysisId").asText();

            System.out.println("🧮 Calculando pontuação para a análise ID: " + analysisId);

            // =========================================================================
            // LÓGICA DE NEGÓCIO (SCORER SIMPLIFICADO)
            // Aqui estamos mockando um motor de regras apenas para fechar o ciclo
            // =========================================================================

            // Extrai a renda da Uber (se existir no json retornado pelo mock)
            double rendaUber = 0;
            if (jsonNode.has("uber") && jsonNode.get("uber").has("ganhos_totais")) {
                rendaUber = jsonNode.get("uber").get("ganhos_totais").asDouble();
            }

            // Extrai a renda do iFood (se existir)
            double rendaIfood = 0;
            if (jsonNode.has("ifood") && jsonNode.get("ifood").has("ganhos_totais")) {
                rendaIfood = jsonNode.get("ifood").get("ganhos_totais").asDouble();
            }

            // Extrai informações do banco (score interno da instituição financeira mockada)
            int scoreBancario = 0;
            if (jsonNode.has("contas") && jsonNode.get("contas").has("score_pagamento")) {
                scoreBancario = jsonNode.get("contas").get("score_pagamento").asInt();
            }

            // 1. Calcula a Renda Total na Gig Economy
            double rendaTotal = rendaUber + rendaIfood;

            // 2. Calcula o Score (0 a 1000) baseado no histórico e renda
            int scoreFinal = 300; // Base
            if (rendaTotal > 3000) scoreFinal += 200;
            if (rendaTotal > 5000) scoreFinal += 200;
            scoreFinal += (scoreBancario / 2); // Usa o comportamento do banco como bônus
            
            // Limita o Score a 1000
            scoreFinal = Math.min(scoreFinal, 1000);

            // 3. Regra de Aprovação
            boolean aprovado = scoreFinal >= 600;
            
            // 4. Calcula o Limite
            int limite = aprovado ? (int) (rendaTotal * 0.4) : 0;
            
            // 5. Gera a explicação
            String explicacao = aprovado 
                ? "Crédito aprovado! Renda consistente na Gig Economy comprovada." 
                : "Crédito negado. Score não atingiu a pontuação mínima exigida (600).";


            // =========================================================================
            // ATUALIZAÇÃO DO BANCO DE DADOS
            // =========================================================================
            
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
        }
    }
}
