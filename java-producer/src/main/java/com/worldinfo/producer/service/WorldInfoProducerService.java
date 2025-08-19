package com.worldinfo.producer.service;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.worldinfo.producer.config.RabbitMQConfig;
import com.worldinfo.producer.model.JsonRpcRequest;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;

@Service
public class WorldInfoProducerService {

    private static final Logger logger = LoggerFactory.getLogger(WorldInfoProducerService.class);

    private final RabbitTemplate rabbitTemplate;
    private final Tracer tracer;

    public WorldInfoProducerService(RabbitTemplate rabbitTemplate, Tracer tracer) {
        this.rabbitTemplate = rabbitTemplate;
        this.tracer = tracer;
    }

    public CompletableFuture<String> sendWorldInfoRequest(String method, Map<String, Object> params) {
        return CompletableFuture.supplyAsync(() -> {
            Span span = tracer.spanBuilder("world_info.rpc.send")
                    .setAttribute("rpc.service", "world_info")
                    .setAttribute("rpc.method", method)
                    .setAttribute("messaging.system", "rabbitmq")
                    .setAttribute("messaging.destination", RabbitMQConfig.WORLD_INFO_QUEUE)
                    .setAttribute("messaging.destination_kind", "queue")
                    .startSpan();

            try {
                String requestId = UUID.randomUUID().toString();
                
                // Create JSON-RPC request with tracing context
                JsonRpcRequest request = JsonRpcRequest.of(requestId, method, params)
                        .withTracing(span.getSpanContext().getTraceId(), span.getSpanContext().getSpanId());

                // Add tracing headers to message
                Message message = MessageBuilder
                        .withBody(serializeRequest(request))
                        .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                        .setHeader("trace_id", span.getSpanContext().getTraceId())
                        .setHeader("span_id", span.getSpanContext().getSpanId())
                        .setTimestamp(Date.from(Instant.now()))
                        .build();

                span.addEvent("sending_message");
                
                logger.info("Sending JSON-RPC request: id={}, method={}, trace_id={}", 
                           requestId, method, span.getSpanContext().getTraceId());

                rabbitTemplate.send(RabbitMQConfig.WORLD_INFO_EXCHANGE, 
                                  RabbitMQConfig.WORLD_INFO_ROUTING_KEY, 
                                  message);

                span.addEvent("message_sent");
                span.setStatus(io.opentelemetry.api.trace.StatusCode.OK);
                
                return requestId;
                
            } catch (Exception e) {
                span.recordException(e);
                span.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR, e.getMessage());
                logger.error("Failed to send world info request", e);
                throw new RuntimeException("Failed to send request", e);
            } finally {
                span.end();
            }
        });
    }

    public CompletableFuture<String> sendExtProviderRequest(String method, Map<String, Object> params) {
        return CompletableFuture.supplyAsync(() -> {
            Span span = tracer.spanBuilder("ext_provider.rpc.send")
                    .setAttribute("rpc.service", "world_info.ext_provider")
                    .setAttribute("rpc.method", method)
                    .setAttribute("messaging.system", "rabbitmq")
                    .setAttribute("messaging.destination", RabbitMQConfig.EXT_PROVIDER_QUEUE)
                    .setAttribute("messaging.destination_kind", "queue")
                    .startSpan();

            try {
                String requestId = UUID.randomUUID().toString();
                
                JsonRpcRequest request = JsonRpcRequest.of(requestId, method, params)
                        .withTracing(span.getSpanContext().getTraceId(), span.getSpanContext().getSpanId());

                Message message = MessageBuilder
                        .withBody(serializeRequest(request))
                        .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                        .setHeader("trace_id", span.getSpanContext().getTraceId())
                        .setHeader("span_id", span.getSpanContext().getSpanId())
                        .setTimestamp(Date.from(Instant.now()))
                        .build();

                span.addEvent("sending_message");
                
                logger.info("Sending ext provider JSON-RPC request: id={}, method={}, trace_id={}", 
                           requestId, method, span.getSpanContext().getTraceId());

                rabbitTemplate.send(RabbitMQConfig.EXT_PROVIDER_EXCHANGE, 
                                  RabbitMQConfig.EXT_PROVIDER_ROUTING_KEY, 
                                  message);

                span.addEvent("message_sent");
                span.setStatus(io.opentelemetry.api.trace.StatusCode.OK);
                
                return requestId;
                
            } catch (Exception e) {
                span.recordException(e);
                span.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR, e.getMessage());
                logger.error("Failed to send ext provider request", e);
                throw new RuntimeException("Failed to send request", e);
            } finally {
                span.end();
            }
        });
    }

    private byte[] serializeRequest(JsonRpcRequest request) {
        try {
            return rabbitTemplate.getMessageConverter()
                    .toMessage(request, new MessageProperties())
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize request", e);
        }
    }
}
