package com.postechhackaton.apigateway.config.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.web.server.WebFilter;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class WebFilterConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @MockBean
    private CustomHeaderInterceptor customHeaderInterceptor;

    @Test
    public void testCustomWebFilterBeanCreation() {
        WebFilter customWebFilter = applicationContext.getBean("customWebFilter", WebFilter.class);
        assertNotNull(customWebFilter);
    }
}