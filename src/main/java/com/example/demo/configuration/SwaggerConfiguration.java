package com.example.demo.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SwaggerConfiguration.class);

    @Value("${keycloak.auth-server-url}")
    public String keycloakUrl;

    @Value("${keycloak.realm}")
    public String realmName;

    public final String SCHEMEKEYCLOAK = "oAuthScheme";

    @Bean
    @ConditionalOnProperty(name = "com.example.demo.auth.type", havingValue = "keycloak", matchIfMissing = true)
    public OpenAPI customOpenAPIKeycloak() {
        final OpenAPI openapi = createOpenAPI();
        openapi.components(new Components().addSecuritySchemes(SCHEMEKEYCLOAK, new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2).in(SecurityScheme.In.HEADER).description("Authentification keycloak")
                .flows(new OAuthFlows().authorizationCode(new OAuthFlow()
                        .authorizationUrl(keycloakUrl + "/realms/" + realmName + "/protocol/openid-connect/auth")
                        .tokenUrl(keycloakUrl + "/realms/" + realmName + "/protocol/openid-connect/token")))));
        return openapi;
    }

    @Bean
    @ConditionalOnProperty(name = "com.example.demo.auth.type", havingValue = "nosecurity")
    public OpenAPI customOpenAPI() {
        final OpenAPI openapi = createOpenAPI();
        return openapi;
    }

    private OpenAPI createOpenAPI() {
        logger.info("surcharge de la configuration swagger");
        final OpenAPI openapi = new OpenAPI()
                .info(new Info().title("swagger d'api test").description("swagger d'api test"));
        return openapi;
    }

    // permet d'ajouter le header Authorization aux header qui vont bien, ici on
    // l'ajoute que au methode qui ne sont pas sur /api/public/** */
    @ConditionalOnProperty(name = "com.example.demo.auth.type", havingValue = "keycloak", matchIfMissing = true)
    @Bean
    public OperationCustomizer ajouterKeycloak() {
        return (operation, handlerMethod) -> {
            if (handlerMethod.getMethod().getName().startsWith("/api/public/")) {
                return operation;
            }
            return operation.addSecurityItem(new SecurityRequirement().addList(SCHEMEKEYCLOAK));
        };
    }
}
