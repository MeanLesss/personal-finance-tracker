package com.example.coreapi.console;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

public class ApiClient {

    public record Response(int status, String body) {
        public boolean ok() {
            return status >= 200 && status < 300;
        }
    }

    private final String baseUrl;
    private final HttpClient http;
    private final ObjectMapper json = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    private String token;

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public String baseUrl() {
        return baseUrl;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isUp() {
        return send("GET", "/v3/api-docs", null, null).ok();
    }

    public String extract(String raw, String field) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            JsonNode node = json.readTree(raw);
            JsonNode value = node.get(field);
            return value == null || value.isNull() ? null : value.asText();
        } catch (IOException e) {
            return null;
        }
    }

    public Response get(String path, Map<String, Object> query) {
        return send("GET", path, query, null);
    }

    public Response post(String path, Map<String, Object> body) {
        return send("POST", path, null, body);
    }

    public Response put(String path, Map<String, Object> body) {
        return send("PUT", path, null, body);
    }

    public Response delete(String path) {
        return send("DELETE", path, null, null);
    }

    public Response send(String method, String path, Map<String, Object> query, Map<String, Object> body) {
        try {
            String url = baseUrl + path;
            if (query != null && !query.isEmpty()) {
                url = url + "?" + buildQuery(query);
            }

            HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(30))
                    .header("Accept", "application/json");

            if (token != null && !token.isBlank()) {
                builder.header("Authorization", "Bearer " + token);
            }

            if ("POST".equals(method) || "PUT".equals(method)) {
                String payload = body == null ? "{}" : json.writeValueAsString(body);
                builder.header("Content-Type", "application/json");
                builder.method(method, HttpRequest.BodyPublishers.ofString(payload));
            } else {
                builder.method(method, HttpRequest.BodyPublishers.noBody());
            }

            HttpResponse<String> response = http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            return new Response(response.statusCode(), response.body());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new Response(-1, "Request interrupted: " + e.getMessage());
        } catch (IOException e) {
            return new Response(-1, "Cannot reach " + baseUrl + " — " + e.getMessage());
        }
    }

    String buildQuery(Map<String, Object> query) {
        StringBuilder sb = new StringBuilder();
        query.forEach((key, value) -> {
            if (value == null) {
                return;
            }
            if (sb.length() > 0) {
                sb.append('&');
            }
            sb.append(key).append('=')
                    .append(URLEncoder.encode(String.valueOf(value), StandardCharsets.UTF_8));
        });
        return sb.toString();
    }

    public String prettyPrint(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        try {
            JsonNode node = json.readTree(raw);
            if (node == null) {
                return raw;
            }
            return json.writerWithDefaultPrettyPrinter().writeValueAsString(node);
        } catch (IOException e) {
            return raw;
        }
    }
}