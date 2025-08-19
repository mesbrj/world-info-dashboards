package com.worldinfo.producer.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String WORLD_INFO_QUEUE = "world_info";
    public static final String WORLD_INFO_EXCHANGE = "world_info_exchange";
    public static final String WORLD_INFO_ROUTING_KEY = "world_info.rpc";

    public static final String EXT_PROVIDER_QUEUE = "world_info.ext_provider";
    public static final String EXT_PROVIDER_EXCHANGE = "world_info_ext_provider_exchange";
    public static final String EXT_PROVIDER_ROUTING_KEY = "world_info.ext_provider.rpc";

    @Bean
    public TopicExchange worldInfoExchange() {
        return new TopicExchange(WORLD_INFO_EXCHANGE);
    }

    @Bean
    public TopicExchange extProviderExchange() {
        return new TopicExchange(EXT_PROVIDER_EXCHANGE);
    }

    @Bean
    public Queue worldInfoQueue() {
        return QueueBuilder.durable(WORLD_INFO_QUEUE).build();
    }

    @Bean
    public Queue extProviderQueue() {
        return QueueBuilder.durable(EXT_PROVIDER_QUEUE).build();
    }

    @Bean
    public Binding worldInfoBinding() {
        return BindingBuilder
                .bind(worldInfoQueue())
                .to(worldInfoExchange())
                .with(WORLD_INFO_ROUTING_KEY);
    }

    @Bean
    public Binding extProviderBinding() {
        return BindingBuilder
                .bind(extProviderQueue())
                .to(extProviderExchange())
                .with(EXT_PROVIDER_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
