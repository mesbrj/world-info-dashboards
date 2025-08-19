package com.worldinfo.producer.model;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record JsonRpcRequest(
        @JsonProperty("jsonrpc")
        @NotBlank
        String jsonrpc,
        
        @JsonProperty("id")
        @NotBlank
        String id,
        
        @JsonProperty("method")
        @NotBlank
        String method,
        
        @JsonProperty("params")
        Map<String, Object> params,
        
        @JsonProperty("timestamp")
        Instant timestamp,
        
        @JsonProperty("trace_id")
        String traceId,
        
        @JsonProperty("span_id")
        String spanId
) {
    public JsonRpcRequest {
        if (jsonrpc == null) {
            jsonrpc = "1.0";
        }
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }
    
    public static JsonRpcRequest of(String id, String method, Map<String, Object> params) {
        return new JsonRpcRequest("1.0", id, method, params, Instant.now(), null, null);
    }
    
    public JsonRpcRequest withTracing(String traceId, String spanId) {
        return new JsonRpcRequest(jsonrpc, id, method, params, timestamp, traceId, spanId);
    }
}
