package com.miguan.laidian.rabbitMQ.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author shixh
 * @Date 2019/12/11
 **/
@Configuration
public class RabbitConfig {
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

}
