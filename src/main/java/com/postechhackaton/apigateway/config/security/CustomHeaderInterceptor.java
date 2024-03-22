package com.postechhackaton.apigateway.config.security;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class CustomHeaderInterceptor implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return exchange.getPrincipal()
                .ofType(JwtAuthenticationToken.class)
                .map(JwtAuthenticationToken::getToken)
                .cast(Jwt.class)
                .map(this::convertJwtToHeaders)
                .map(headers -> {
                    ServerHttpRequestDecorator requestDecorator = new CustomHeaderHttpRequestDecorator(exchange.getRequest(), headers);
                    ServerWebExchange exchangeDecorator = new ServerWebExchangeDecorator(exchange) {
                        @Override
                        public ServerHttpRequest getRequest() {
                            return requestDecorator;
                        }
                    };
                    return exchangeDecorator;
                })
                .defaultIfEmpty(exchange)
                .flatMap(chain::filter);
    }

    private HttpHeaders convertJwtToHeaders(Jwt jwt) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("email", jwt.getClaimAsString("email"));
        headers.add("role", jwt.getClaimAsString("azp"));
        headers.add("username", jwt.getClaimAsString("preferred_username"));
        return headers;
    }

    private static class CustomHeaderHttpRequestDecorator extends ServerHttpRequestDecorator {
        private final HttpHeaders headers;

        public CustomHeaderHttpRequestDecorator(ServerHttpRequest delegate, HttpHeaders headers) {
            super(delegate);
            this.headers = headers;
        }

        @Override
        public HttpHeaders getHeaders() {
            return headers;
        }
    }
}
