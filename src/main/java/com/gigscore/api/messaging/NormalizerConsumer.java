package com.gigscore.api.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigscore.api.dto.NormalizedFinancialData;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class NormalizerConsumer {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public NormalizerConsumer(ObjectMapper objectMapper, KafkaTemplate<String, Object> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "financial-data-collected", groupId = "gigscore-normalizer-group")
    public void normalize(String pacoteFinanceiroBruto) {
        System.out.println("📥 Mensagem recebida no Kafka (financial-data-collected): Iniciando Normalização...");

        try {
            JsonNode rootNode = objectMapper.readTree(pacoteFinanceiroBruto);
            String analysisId = rootNode.get("analysisId").asText();
            String cpf = rootNode.get("cpf").asText();

            // --- LÓGICA DE NORMALIZAÇÃO ---

            // 1. Normaliza Renda
            BigDecimal rendaIfood = extrairBigDecimal(rootNode, "ifood", "ganhos_totais");
            BigDecimal rendaUber = extrairBigDecimal(rootNode, "uber", "ganhos_totais");
            BigDecimal rendaTotal = rendaIfood.add(rendaUber);

            // 2. Normaliza Reputação
            double avaliacaoIfood = extrairDouble(rootNode, "ifood", "avaliacao");
            double avaliacaoUber = extrairDouble(rootNode, "uber", "avaliacao");
            double reputacaoMedia = (avaliacaoIfood + avaliacaoUber > 0) ? (avaliacaoIfood + avaliacaoUber) / 2.0 : 0;

            // 3. Normaliza Total de Gigs
            int entregasIfood = extrairInt(rootNode, "ifood", "entregas_concluidas");
            int corridasUber = extrairInt(rootNode, "uber", "corridas_concluidas");
            int totalGigs = entregasIfood + corridasUber;

            // 4. Normaliza Score de Pagamento (Open Finance)
            int scorePagamento = extrairInt(rootNode, "contas", "score_pagamento");

            // --- CRIAÇÃO DO MODELO CANÔNICO ---
            NormalizedFinancialData dadosNormalizados = new NormalizedFinancialData(
                    analysisId,
                    cpf,
                    rendaTotal,
                    reputacaoMedia,
                    totalGigs,
                    scorePagamento
            );

            System.out.println("✅ Dados Normalizados: " + dadosNormalizados);

            // --- PUBLICAÇÃO DO PRÓXIMO EVENTO ---
            // IMPORTANTE: Como alteramos o application.properties para usar StringSerializer em tudo,
            // precisamos serializar manualmente o objeto para JSON String antes de enviar para o Kafka!
            String dadosNormalizadosJson = objectMapper.writeValueAsString(dadosNormalizados);
            
            kafkaTemplate.send("normalized-data-ready", analysisId, dadosNormalizadosJson);
            System.out.println("🚀 Pacote de dados normalizados publicado no tópico 'normalized-data-ready'");

        } catch (Exception e) {
            System.err.println("❌ Erro ao normalizar os dados: " + e.getMessage());
        }
    }

    // Métodos utilitários para extrair dados do JSON de forma segura
    private BigDecimal extrairBigDecimal(JsonNode root, String parent, String field) {
        if (root.has(parent) && root.get(parent).has(field)) {
            return new BigDecimal(root.get(parent).get(field).asText());
        }
        return BigDecimal.ZERO;
    }

    private double extrairDouble(JsonNode root, String parent, String field) {
        if (root.has(parent) && root.get(parent).has(field)) {
            return root.get(parent).get(field).asDouble();
        }
        return 0.0;
    }

    private int extrairInt(JsonNode root, String parent, String field) {
        if (root.has(parent) && root.get(parent).has(field)) {
            return root.get(parent).get(field).asInt();
        }
        return 0;
    }
}
