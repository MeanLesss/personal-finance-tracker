package com.example.coreapi.console;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleApiClientTest {

    private final ApiClient api = new ApiClient("http://localhost:9090/");

    @Test
    void baseUrlIsNormalizedWithoutTrailingSlash() {
        assertEquals("http://localhost:9090", api.baseUrl());
    }

    @Test
    void buildQueryEncodesValues() {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("page", 1);
        query.put("size", 10);
        assertEquals("page=1&size=10", api.buildQuery(query));
    }

    @Test
    void buildQueryIgnoresNullValues() {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("year", 2026);
        query.put("month", null);
        assertEquals("year=2026", api.buildQuery(query));
    }

    @Test
    void payloadSkipsNullValues() {
        Map<String, Object> body = ConsoleApiClient.payload(
                "name", "Alice",
                "balance", null,
                "userId", 7L);
        assertEquals(2, body.size());
        assertEquals("Alice", body.get("name"));
        assertEquals(7L, body.get("userId"));
        assertFalse(body.containsKey("balance"));
    }

    @Test
    void prettyPrintFormatsJson() {
        String pretty = api.prettyPrint("{\"code\":\"200\"}");
        assertTrue(pretty.contains("\"code\""));
        assertTrue(pretty.contains("\n"));
    }

    @Test
    void prettyPrintPassesThroughNonJson() {
        assertEquals("Connection refused", api.prettyPrint("Connection refused"));
    }

    @Test
    void prettyPrintHandlesBlank() {
        assertEquals("", api.prettyPrint("   "));
    }
}