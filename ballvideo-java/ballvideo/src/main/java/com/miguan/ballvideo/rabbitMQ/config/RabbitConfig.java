package com.miguan.ballvideo.rabbitMQ.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author shixh
 * @Date 2019/12/11
 **/
@Configuration
public class RabbitConfig {

    @Value("${spring.rabbitmq.addresses}")
    private String addresses;

    private String virtualHost = "/";

    @Value("${spring.rabbitmq.port}")
    private Integer port;

    @Value("${spring.rabbitmq.username}")
    private String username;
    @Value("${spring.rabbitmq.password}")
    private String password;

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange("fanout-exchange");
    }
    @Bean
    public Binding binding0(FanoutExchange fanoutExchange, Queue systemQueue) {
        return BindingBuilder.bind(systemQueue).to(fanoutExchange);
    }
    @Bean
    public Queue systemQueue() {
        return new AnonymousQueue();
    }

    @Bean
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(addresses + ":" + port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        return connectionFactory;
    }

    @Bean
    public org.springframework.amqp.rabbit.core.RabbitAdmin rabbitAdmin(CachingConnectionFactory connectionFactory){
        return new org.springframework.amqp.rabbit.core.RabbitAdmin(connectionFactory);
    }


}
