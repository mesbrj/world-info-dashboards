package com.worldinfo.producer.model;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record JsonRpcResponse(
        @JsonProperty("jsonrpc")
        String jsonrpc,
        
        @JsonProperty("id")
        String id,
        
        @JsonProperty("result")
        Object result,
        
        @JsonProperty("error")
        JsonRpcError error,
        
        @JsonProperty("timestamp")
        Instant timestamp,
        
        @JsonProperty("trace_id")
        String traceId,
        
        @JsonProperty("span_id")
        String spanId
) {
    public JsonRpcResponse {
        if (jsonrpc == null) {
            jsonrpc = "1.0";
        }
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }
    
    public static JsonRpcResponse success(String id, Object result) {
        return new JsonRpcResponse("1.0", id, result, null, Instant.now(), null, null);
    }
    
    public static JsonRpcResponse error(String id, JsonRpcError error) {
        return new JsonRpcResponse("1.0", id, null, error, Instant.now(), null, null);
    }
    
    public JsonRpcResponse withTracing(String traceId, String spanId) {
        return new JsonRpcResponse(jsonrpc, id, result, error, timestamp, traceId, spanId);
    }
}
