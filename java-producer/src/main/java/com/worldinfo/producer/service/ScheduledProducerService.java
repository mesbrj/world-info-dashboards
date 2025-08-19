package com.worldinfo.producer.service;

import java.time.Instant;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;

@Service
@ConditionalOnProperty(name = "app.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class ScheduledProducerService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledProducerService.class);

    private final WorldInfoProducerService producerService;
    private final Tracer tracer;
    private final Random random = new Random();

    private final String[] worldInfoMethods = {
            "getWeatherInfo", "getTimeInfo", "getLocationInfo", "getCurrencyInfo"
    };

    private final String[] extProviderMethods = {
            "fetchWeatherData", "fetchGeoLocation", "fetchImageOfDay", "fetchNewsData"
    };

    public ScheduledProducerService(WorldInfoProducerService producerService, Tracer tracer) {
        this.producerService = producerService;
        this.tracer = tracer;
    }

    @Scheduled(fixedDelay = 30000, initialDelay = 10000) // Every 30 seconds
    public void sendWorldInfoRequests() {
        Span span = tracer.spanBuilder("scheduled.world_info.send")
                .setAttribute("scheduler.type", "fixed_delay")
                .setAttribute("scheduler.interval", "30s")
                .startSpan();

        try {
            String method = worldInfoMethods[random.nextInt(worldInfoMethods.length)];
            Map<String, Object> params = generateParams(method);

            logger.info("Scheduled task: sending world info request - method: {}", method);

            span.addEvent("sending_request");
            producerService.sendWorldInfoRequest(method, params)
                    .whenComplete((requestId, throwable) -> {
                        if (throwable != null) {
                            span.recordException(throwable);
                            span.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR, throwable.getMessage());
                            logger.error("Scheduled world info request failed", throwable);
                        } else {
                            span.setStatus(io.opentelemetry.api.trace.StatusCode.OK);
                            logger.info("Scheduled world info request sent successfully: {}", requestId);
                        }
                    });

        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR, e.getMessage());
            logger.error("Error in scheduled world info task", e);
        } finally {
            span.end();
        }
    }

    @Scheduled(fixedDelay = 45000, initialDelay = 20000) // Every 45 seconds
    public void sendExtProviderRequests() {
        Span span = tracer.spanBuilder("scheduled.ext_provider.send")
                .setAttribute("scheduler.type", "fixed_delay")
                .setAttribute("scheduler.interval", "45s")
                .startSpan();

        try {
            String method = extProviderMethods[random.nextInt(extProviderMethods.length)];
            Map<String, Object> params = generateParams(method);

            logger.info("Scheduled task: sending ext provider request - method: {}", method);

            span.addEvent("sending_request");
            producerService.sendExtProviderRequest(method, params)
                    .whenComplete((requestId, throwable) -> {
                        if (throwable != null) {
                            span.recordException(throwable);
                            span.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR, throwable.getMessage());
                            logger.error("Scheduled ext provider request failed", throwable);
                        } else {
                            span.setStatus(io.opentelemetry.api.trace.StatusCode.OK);
                            logger.info("Scheduled ext provider request sent successfully: {}", requestId);
                        }
                    });

        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR, e.getMessage());
            logger.error("Error in scheduled ext provider task", e);
        } finally {
            span.end();
        }
    }

    private Map<String, Object> generateParams(String method) {
        return switch (method) {
            case "getWeatherInfo", "fetchWeatherData" -> Map.of(
                    "location", "New York",
                    "units", "metric",
                    "timestamp", Instant.now().toString()
            );
            case "getTimeInfo" -> Map.of(
                    "timezone", "UTC",
                    "format", "ISO8601",
                    "timestamp", Instant.now().toString()
            );
            case "getLocationInfo", "fetchGeoLocation" -> Map.of(
                    "lat", 40.7128 + (random.nextGaussian() * 0.1),
                    "lon", -74.0060 + (random.nextGaussian() * 0.1),
                    "timestamp", Instant.now().toString()
            );
            case "getCurrencyInfo" -> Map.of(
                    "from", "USD",
                    "to", "EUR",
                    "amount", 100.0,
                    "timestamp", Instant.now().toString()
            );
            case "fetchImageOfDay" -> Map.of(
                    "category", "nature",
                    "resolution", "1920x1080",
                    "timestamp", Instant.now().toString()
            );
            case "fetchNewsData" -> Map.of(
                    "category", "technology",
                    "limit", 10,
                    "timestamp", Instant.now().toString()
            );
            default -> Map.of(
                    "timestamp", Instant.now().toString(),
                    "source", "scheduled-task"
            );
        };
    }
}
