package com.miguan.idmapping.common.config.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.Map;

/**
 * @author zhongli
 * @date 2020-09-04 
 *
 */
@Configuration
public class KafkaConfig {

    private final KafkaProperties properties;

    public KafkaConfig(KafkaProperties properties) {
        this.properties = properties;
    }

    @Bean("kafkaListenerContainerFactory2Clickhouse")
    @ConditionalOnMissingBean(name = "kafkaListenerContainerFactory2Clickhouse")
    ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ObjectProvider<ConsumerFactory<Object, Object>> kafkaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        Map<String, Object> p = this.properties.buildConsumerProperties();
        p.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000);
        factory.setBatchListener(true);
        configurer.configure(factory, new DefaultKafkaConsumerFactory<>(p));
        return factory;
    }
}
