package com.gigscore.api.config;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.RecordInterceptor;
import org.springframework.kafka.support.ProducerListener;

/**
 * Configuração de Interceptadores para Observabilidade de Eventos Kafka (Task 2).
 */
@Configuration
public class KafkaConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaConfig.class);

    /**
     * Intercepta e loga todas as mensagens PRODUZIDAS (Enviadas) para o Kafka.
     */
    @Bean
    public ProducerListener<Object, Object> producerListener() {
        return new ProducerListener<>() {
            @Override
            public void onSuccess(ProducerRecord<Object, Object> producerRecord, RecordMetadata recordMetadata) {
                log.info("📤 [KAFKA PRODUCER] Evento Publicado | Tópico: {} | Partição: {} | Offset: {} | Payload: {}",
                        recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset(), producerRecord.value());
            }

            @Override
            public void onError(ProducerRecord<Object, Object> producerRecord, RecordMetadata recordMetadata, Exception exception) {
                log.error("❌ [KAFKA PRODUCER] Falha ao Publicar Evento | Tópico: {} | Payload: {}",
                        producerRecord.topic(), producerRecord.value(), exception);
            }
        };
    }

    /**
     * Intercepta e loga todas as mensagens CONSUMIDAS (Lidas) do Kafka.
     */
    @Bean
    public RecordInterceptor<Object, Object> recordInterceptor() {
        return new RecordInterceptor<>() {
            @Override
            public ConsumerRecord<Object, Object> intercept(ConsumerRecord<Object, Object> record, org.apache.kafka.clients.consumer.Consumer<Object, Object> consumer) {
                log.info("📥 [KAFKA CONSUMER] Evento Recebido | Tópico: {} | Partição: {} | Offset: {} | Payload: {}",
                        record.topic(), record.partition(), record.offset(), record.value());
                return record;
            }
        };
    }

    /**
     * Sobrescreve a fábrica padrão do Spring Kafka para injetar o nosso RecordInterceptor nos Consumers.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<Object, Object> kafkaConsumerFactory,
            RecordInterceptor<Object, Object> recordInterceptor) {
        
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, kafkaConsumerFactory);
        factory.setRecordInterceptor(recordInterceptor);
        
        return factory;
    }
}
