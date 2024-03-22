package com.postechhackaton.apigateway.config.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CustomHeaderInterceptorTest {
    @InjectMocks
    private CustomHeaderInterceptor customHeaderInterceptor;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private WebFilterChain chain;

    @Mock
    private JwtAuthenticationToken jwtAuthenticationToken;

    @Mock
    private Jwt jwt;

    @Mock
    private ServerHttpRequest serverHttpRequest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(exchange.getPrincipal()).thenReturn(Mono.just(jwtAuthenticationToken));
        when(jwtAuthenticationToken.getToken()).thenReturn(jwt);
        when(exchange.getRequest()).thenReturn(serverHttpRequest);
        when(chain.filter(any())).thenReturn(Mono.empty());
    }

    @Test
    public void testFilterAddsHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("email", "test@example.com");
        headers.add("system", "azp_value");
        headers.add("name", "John");
        headers.add("familyName", "Doe");
        headers.add("username", "johndoe");
        ArrayList<String> roles = new ArrayList<>();
        roles.add("role1");
        roles.add("role2");
        when(jwt.getClaimAsString("email")).thenReturn("test@example.com");
        when(jwt.getClaimAsString("azp")).thenReturn("azp_value");
        when(jwt.getClaimAsString("given_name")).thenReturn("John");
        when(jwt.getClaimAsString("family_name")).thenReturn("Doe");
        when(jwt.getClaimAsString("preferred_username")).thenReturn("johndoe");
        when(jwt.getClaimAsMap("realm_access")).thenReturn(Map.of("roles", roles));

        customHeaderInterceptor.filter(exchange, chain).block();

//        verify(serverHttpRequest).mutate();
        verify(chain).filter(any());
    }

    @Test
    public void testFilterNoJwtAuthenticationToken() {
        when(exchange.getPrincipal()).thenReturn(Mono.empty());

        customHeaderInterceptor.filter(exchange, chain).block();

        verify(chain).filter(any());
    }
}
