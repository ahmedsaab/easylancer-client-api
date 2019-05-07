package com.easylancer.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;

public class PayloadLoggingWebFilter implements WebFilter {

    private final Logger logger;
    private final boolean encodeBytes = false;
    private final ObjectMapper om = new ObjectMapper();


    public PayloadLoggingWebFilter(Logger logger) {
        this(logger, false);
    }

    private PayloadLoggingWebFilter(Logger logger, boolean encodeBytes) {
        this.logger = logger;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (logger.isInfoEnabled()) {
            return chain.filter(decorate(exchange));
        } else {
            return chain.filter(exchange);
        }
    }

    private ServerWebExchange decorate(ServerWebExchange exchange) {
        final ServerHttpRequest decorated = new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public Flux<DataBuffer> getBody() {
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                return super.getBody().map(dataBuffer -> {
                    try {
                        Channels.newChannel(baos).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
                    } catch (IOException e) {
                        logger.error("Unable to log input request due to an error", e);
                    }
                    return dataBuffer;
                }).doOnComplete(() -> {
                    try {
                        logger.info(om.readTree(baos.toByteArray()).toString());
                    } catch (IOException e) {
                        logger.error("Failed to parse response body to Json", e);
                    }
                });
            }
        };

        return new ServerWebExchangeDecorator(exchange) {
            @Override
            public ServerHttpRequest getRequest() {
                return decorated;
            }
        };
    }
}