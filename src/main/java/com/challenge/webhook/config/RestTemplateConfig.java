package com.challenge.webhook.config;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.stream.Collectors;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // Create Apache HttpClient with proper configuration
        HttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(
                        PoolingHttpClientConnectionManagerBuilder.create()
                                .setDefaultConnectionConfig(
                                        org.apache.hc.client5.http.config.ConnectionConfig.custom()
                                                .setConnectTimeout(Timeout.ofSeconds(10))
                                                .setSocketTimeout(Timeout.ofSeconds(30))
                                                .build())
                                .build())
                .build();
        
        HttpComponentsClientHttpRequestFactory requestFactory = 
                new HttpComponentsClientHttpRequestFactory(httpClient);
        
        RestTemplate restTemplate = builder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(30))
                .errorHandler(new CustomResponseErrorHandler())
                .interceptors(new LoggingInterceptor())
                .build();
        
        // Set the request factory directly (no buffering to avoid streaming mode issues)
        restTemplate.setRequestFactory(requestFactory);
        
        return restTemplate;
    }

    /**
     * Custom error handler for non-2xx responses
     */
    private static class CustomResponseErrorHandler extends DefaultResponseErrorHandler {
        
        private static final Logger errorLogger = LoggerFactory.getLogger(CustomResponseErrorHandler.class);

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            try {
                String responseBody;
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
                    responseBody = reader.lines().collect(Collectors.joining("\n"));
                }

                errorLogger.error("HTTP Error Response - Status: {}, Body: {}", 
                        response.getStatusCode(), responseBody);
            } catch (Exception e) {
                errorLogger.error("HTTP Error Response - Status: {} (could not read body: {})", 
                        response.getStatusCode(), e.getMessage());
            }

            super.handleError(response);
        }
    }

    /**
     * Logging interceptor for request/response details
     */
    private static class LoggingInterceptor implements ClientHttpRequestInterceptor {

        private static final Logger interceptorLogger = LoggerFactory.getLogger(LoggingInterceptor.class);

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, 
                ClientHttpRequestExecution execution) throws IOException {
            
            logRequest(request, body);
            
            ClientHttpResponse response = execution.execute(request, body);
            
            logResponse(response);
            
            return response;
        }

        private void logRequest(HttpRequest request, byte[] body) {
            interceptorLogger.info("HTTP Request - Method: {}, URI: {}", 
                    request.getMethod(), request.getURI());
            interceptorLogger.debug("Request Headers: {}", request.getHeaders());
            
            if (body.length > 0) {
                interceptorLogger.debug("Request Body: {}", new String(body, StandardCharsets.UTF_8));
            }
        }

        private void logResponse(ClientHttpResponse response) {
            try {
                interceptorLogger.info("HTTP Response - Status Code: {}", response.getStatusCode());
                interceptorLogger.debug("Response Headers: {}", response.getHeaders());
            } catch (IOException e) {
                interceptorLogger.warn("Failed to log response details: {}", e.getMessage());
            }
        }
    }
}
