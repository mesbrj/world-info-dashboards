package com.worldinfo.producer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JsonRpcError(
        @JsonProperty("code")
        int code,
        
        @JsonProperty("message")
        String message,
        
        @JsonProperty("data")
        Object data
) {
    public static JsonRpcError parseError() {
        return new JsonRpcError(-32700, "Parse error", null);
    }
    
    public static JsonRpcError invalidRequest() {
        return new JsonRpcError(-32600, "Invalid Request", null);
    }
    
    public static JsonRpcError methodNotFound() {
        return new JsonRpcError(-32601, "Method not found", null);
    }
    
    public static JsonRpcError invalidParams() {
        return new JsonRpcError(-32602, "Invalid params", null);
    }
    
    public static JsonRpcError internalError() {
        return new JsonRpcError(-32603, "Internal error", null);
    }
    
    public static JsonRpcError serverError(int code, String message) {
        return new JsonRpcError(code, message, null);
    }
}
