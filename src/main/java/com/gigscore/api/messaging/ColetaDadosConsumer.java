package com.gigscore.api.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigscore.api.service.ContasService;
import com.gigscore.api.service.IfoodService;
import com.gigscore.api.service.UberService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ColetaDadosConsumer {

    private final IfoodService ifoodService;
    private final UberService uberService;
    private final ContasService contasService;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ColetaDadosConsumer(IfoodService ifoodService, UberService uberService, ContasService contasService, ObjectMapper objectMapper, KafkaTemplate<String, Object> kafkaTemplate) {
        this.ifoodService = ifoodService;
        this.uberService = uberService;
        this.contasService = contasService;
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "credit-analysis-requested", groupId = "gigscore-group")
    public void escutarSolicitacao(String mensagem) {
        System.out.println("📥 Mensagem recebida no Kafka (credit-analysis-requested): " + mensagem);

        try {
            // Extrai o CPF e ID da análise do JSON recebido
            JsonNode jsonNode = objectMapper.readTree(mensagem);
            String analysisId = jsonNode.get("analysisId").asText();
            String cpf = jsonNode.get("cpf").asText();

            System.out.println("🔄 Iniciando coleta agregada e assíncrona (iFood, Uber e Contas) para o CPF: " + cpf);

            // 1. Coleta dados agregados do iFood
            String dadosAgregadosIfood = ifoodService.buscarTodosOsDados(cpf);
            System.out.println("✅ Dados do iFood agregados com sucesso: " + dadosAgregadosIfood);

            // 2. Coleta dados agregados da Uber
            String dadosAgregadosUber = uberService.buscarTodosOsDados(cpf);
            System.out.println("✅ Dados da Uber agregados com sucesso: " + dadosAgregadosUber);

            // 3. Coleta dados agregados de Contas (Open Finance)
            String dadosAgregadosContas = contasService.buscarTodosOsDados(cpf);
            System.out.println("✅ Dados de Contas agregados com sucesso: " + dadosAgregadosContas);

            // Agrupa todos os dados para mandar para o Scorer
            String pacoteFinanceiro = String.format(
                    "{\"analysisId\": \"%s\", \"cpf\": \"%s\", \"ifood\": %s, \"uber\": %s, \"contas\": %s}",
                    analysisId, cpf, dadosAgregadosIfood, dadosAgregadosUber, dadosAgregadosContas
            );

            // Publica o novo evento no Kafka (financial-data-collected)
            kafkaTemplate.send("financial-data-collected", analysisId, pacoteFinanceiro);
            System.out.println("🚀 Pacote financeiro completo publicado no tópico 'financial-data-collected' para o ID: " + analysisId);

        } catch (Exception e) {
            System.err.println("❌ Erro ao processar mensagem do Kafka ou coletar dados: " + e.getMessage());
        }
    }
}
