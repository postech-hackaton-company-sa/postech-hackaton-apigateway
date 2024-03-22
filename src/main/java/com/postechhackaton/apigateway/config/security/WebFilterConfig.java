package com.postechhackaton.apigateway.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.WebFilter;

@Configuration
public class WebFilterConfig {

    @Bean
    public WebFilter customWebFilter() {
        return new CustomHeaderInterceptor();
    }


}
