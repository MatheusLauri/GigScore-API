package com.gigscore.api.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigscore.api.service.IfoodService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ColetaDadosConsumer {

    private final IfoodService ifoodService;
    private final ObjectMapper objectMapper;

    public ColetaDadosConsumer(IfoodService ifoodService, ObjectMapper objectMapper) {
        this.ifoodService = ifoodService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "credit-analysis-requested", groupId = "gigscore-group")
    public void escutarSolicitacao(String mensagem) {
        System.out.println("📥 Mensagem recebida no Kafka (credit-analysis-requested): " + mensagem);

        try {
            // Extrai o CPF do JSON recebido
            JsonNode jsonNode = objectMapper.readTree(mensagem);
            String cpf = jsonNode.get("cpf").asText();

            System.out.println("🔄 Iniciando coleta agregada e assíncrona no iFood para o CPF: " + cpf);
            
            // Agora o Consumer faz UMA única chamada para o Service,
            // e o Apache Camel resolve todo o paralelismo e a agregação dos 3 endpoints!
            String dadosAgregadosIfood = ifoodService.buscarTodosOsDados(cpf);
            
            System.out.println("✅ Dados do iFood agregados com sucesso: " + dadosAgregadosIfood);

            // TODO: Aqui será publicado um novo evento no Kafka (ex: financial-data-collected) com os dados agregados
            System.out.println("🚀 Pacote do iFood pronto para a próxima etapa (Normalizer)!");

        } catch (Exception e) {
            System.err.println("❌ Erro ao processar mensagem do Kafka ou coletar dados: " + e.getMessage());
        }
    }
}
