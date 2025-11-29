package com.restapi.microtech.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final AuthInterceptor authInterceptor;
    private final RoleInterceptor roleInterceptor;

    private List<String> OpenRoutes = new ArrayList<>(List.of(
            "/auth/**"));

    private List<String> ClientRoutes = new ArrayList<>(List.of(
            "/clients/{id}/profile",
            "/clients/{id}/loyalty-tier",
            "/clients/{id}/stats",
            "/clients/{id}/orders",
            "/produits"));

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(OpenRoutes);

        ClientRoutes.addAll(OpenRoutes);
        registry.addInterceptor(roleInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(ClientRoutes);
    }
}
