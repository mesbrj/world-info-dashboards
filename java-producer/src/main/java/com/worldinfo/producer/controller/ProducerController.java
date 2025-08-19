package com.worldinfo.producer.controller;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.worldinfo.producer.service.WorldInfoProducerService;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;

@RestController
@RequestMapping("/api/v1")
public class ProducerController {

    private static final Logger logger = LoggerFactory.getLogger(ProducerController.class);

    private final WorldInfoProducerService producerService;
    private final Tracer tracer;

    public ProducerController(WorldInfoProducerService producerService, Tracer tracer) {
        this.producerService = producerService;
        this.tracer = tracer;
    }

    @PostMapping("/world-info/send")
    public CompletableFuture<ResponseEntity<Map<String, String>>> sendWorldInfoRequest(
            @RequestParam String method,
            @RequestBody(required = false) Map<String, Object> params) {
        
        Span span = tracer.spanBuilder("http.request.world_info")
                .setAttribute("http.method", "POST")
                .setAttribute("http.route", "/api/v1/world-info/send")
                .setAttribute("rpc.method", method)
                .startSpan();

        try {
            logger.info("Received request to send world info method: {}", method);
            
            return producerService.sendWorldInfoRequest(method, params != null ? params : Map.of())
                    .thenApply(requestId -> {
                        span.setStatus(io.opentelemetry.api.trace.StatusCode.OK);
                        span.end();
                        return ResponseEntity.ok(Map.of(
                                "success", "true",
                                "requestId", requestId,
                                "method", method,
                                "traceId", span.getSpanContext().getTraceId()
                        ));
                    })
                    .exceptionally(throwable -> {
                        span.recordException(throwable);
                        span.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR, throwable.getMessage());
                        span.end();
                        logger.error("Failed to send world info request", throwable);
                        return ResponseEntity.internalServerError().body(Map.of(
                                "success", "false",
                                "error", throwable.getMessage(),
                                "method", method
                        ));
                    });
                    
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR, e.getMessage());
            span.end();
            throw e;
        }
    }

    @PostMapping("/ext-provider/send")
    public CompletableFuture<ResponseEntity<Map<String, String>>> sendExtProviderRequest(
            @RequestParam String method,
            @RequestBody(required = false) Map<String, Object> params) {
        
        Span span = tracer.spanBuilder("http.request.ext_provider")
                .setAttribute("http.method", "POST")
                .setAttribute("http.route", "/api/v1/ext-provider/send")
                .setAttribute("rpc.method", method)
                .startSpan();

        try {
            logger.info("Received request to send ext provider method: {}", method);
            
            return producerService.sendExtProviderRequest(method, params != null ? params : Map.of())
                    .thenApply(requestId -> {
                        span.setStatus(io.opentelemetry.api.trace.StatusCode.OK);
                        span.end();
                        return ResponseEntity.ok(Map.of(
                                "success", "true",
                                "requestId", requestId,
                                "method", method,
                                "traceId", span.getSpanContext().getTraceId()
                        ));
                    })
                    .exceptionally(throwable -> {
                        span.recordException(throwable);
                        span.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR, throwable.getMessage());
                        span.end();
                        logger.error("Failed to send ext provider request", throwable);
                        return ResponseEntity.internalServerError().body(Map.of(
                                "success", "false",
                                "error", throwable.getMessage(),
                                "method", method
                        ));
                    });
                    
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR, e.getMessage());
            span.end();
            throw e;
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "java-producer",
                "timestamp", System.currentTimeMillis()
        ));
    }
}
