package com.hrms.db.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrms.db.facade.HRMSDatabaseFacade;
import com.hrms.db.server.RepositoryGateway.GatewayException;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Embedded HTTP server exposing the repository gateway and a small admin frontend.
 */
public class HRMSHttpGatewayServer {

    private static final int DEFAULT_PORT = 18080;

    public static void main(String[] args) throws IOException {
        int port = resolvePort();

        HRMSDatabaseFacade.getInstance().initialize();
        RepositoryGateway gateway = new RepositoryGateway();
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/api/health", exchange -> handleJson(exchange, gateway.getMapper(), () -> gateway.getHealth()));
        server.createContext("/api/dashboard", exchange -> handleJson(exchange, gateway.getMapper(), gateway::getDashboard));
        server.createContext("/api/repositories", exchange -> handleJson(exchange, gateway.getMapper(), gateway::listRepositories));
        server.createContext("/api/errors", exchange -> handleJson(exchange, gateway.getMapper(), () -> gateway.getRecentErrors(50)));
        server.createContext("/api/invoke", exchange -> handleJson(exchange, gateway.getMapper(), () -> {
            ensureMethod(exchange, "POST");
            Map<String, Object> body = gateway.parseRequestBody(readBody(exchange));
            String repository = String.valueOf(body.getOrDefault("repository", ""));
            String method = String.valueOf(body.getOrDefault("method", ""));
            Object argsValue = body.get("args");
            List<Object> argsList = argsValue instanceof List<?> list ? (List<Object>) list : List.of();
            return gateway.invoke(repository, method, argsList);
        }));
        server.createContext("/", new StaticHandler());

        server.setExecutor(Executors.newCachedThreadPool());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop(0);
            HRMSDatabaseFacade.getInstance().shutdown();
        }));

        server.start();
        System.out.println("[HRMSHttpGatewayServer] Listening on http://localhost:" + port);
    }

    private static int resolvePort() {
        String env = System.getenv("HRMS_HTTP_PORT");
        if (env == null || env.isBlank()) {
            return DEFAULT_PORT;
        }
        try {
            return Integer.parseInt(env.trim());
        } catch (NumberFormatException ex) {
            return DEFAULT_PORT;
        }
    }

    private static void handleJson(HttpExchange exchange, ObjectMapper mapper, JsonSupplier supplier) throws IOException {
        addCors(exchange.getResponseHeaders());
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }

        try {
            Object payload = supplier.get();
            writeJson(exchange, 200, mapper.writeValueAsBytes(payload));
        } catch (GatewayException ex) {
            writeJson(exchange, ex.getStatusCode(), mapper.writeValueAsBytes(Map.of(
                    "error", ex.getMessage(),
                    "status", ex.getStatusCode()
            )));
        } catch (Exception ex) {
            writeJson(exchange, 500, mapper.writeValueAsBytes(Map.of(
                    "error", ex.getMessage(),
                    "status", 500
            )));
        }
    }

    private static void ensureMethod(HttpExchange exchange, String allowedMethod) {
        if (!allowedMethod.equalsIgnoreCase(exchange.getRequestMethod())) {
            throw new GatewayException(405, "Method not allowed. Use " + allowedMethod + ".");
        }
    }

    private static void writeJson(HttpExchange exchange, int statusCode, byte[] body) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, body.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(body);
        }
    }

    private static String readBody(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static void addCors(Headers headers) {
        headers.set("Access-Control-Allow-Origin", "*");
        headers.set("Access-Control-Allow-Headers", "Content-Type");
        headers.set("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
        headers.set("Cache-Control", "no-store");
    }

    private interface JsonSupplier {
        Object get() throws Exception;
    }

    private static final class StaticHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCors(exchange.getResponseHeaders());
            String path = exchange.getRequestURI().getPath();
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                exchange.close();
                return;
            }

            String resourcePath = mapResource(path);
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
                if (inputStream == null) {
                    byte[] body = "Not Found".getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(404, body.length);
                    try (OutputStream outputStream = exchange.getResponseBody()) {
                        outputStream.write(body);
                    }
                    return;
                }

                byte[] body = inputStream.readAllBytes();
                exchange.getResponseHeaders().set("Content-Type", contentType(resourcePath));
                exchange.sendResponseHeaders(200, body.length);
                try (OutputStream outputStream = exchange.getResponseBody()) {
                    outputStream.write(body);
                }
            }
        }

        private String mapResource(String requestPath) {
            if (requestPath == null || requestPath.equals("/") || requestPath.isBlank()) {
                return "static/index.html";
            }
            String cleaned = requestPath.startsWith("/") ? requestPath.substring(1) : requestPath;
            return "static/" + cleaned;
        }

        private String contentType(String resourcePath) {
            if (resourcePath.endsWith(".css")) return "text/css; charset=utf-8";
            if (resourcePath.endsWith(".js")) return "application/javascript; charset=utf-8";
            if (resourcePath.endsWith(".html")) return "text/html; charset=utf-8";
            return "text/plain; charset=utf-8";
        }
    }
}
