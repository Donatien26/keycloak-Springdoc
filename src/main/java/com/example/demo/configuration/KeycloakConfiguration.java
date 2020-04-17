package com.example.demo.configuration;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@Configuration
@EnableWebSecurity
@ConditionalOnProperty(name = "com.example.demo.auth.type", havingValue = "keycloak", matchIfMissing = true)
@ComponentScan(basePackageClasses = KeycloakSecurityComponents.class)
public class KeycloakConfiguration extends KeycloakWebSecurityConfigurerAdapter {
    /**
     * Defines the session authentication strategy.
     */
    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    /**
     * Required to handle spring boot configurations
     *
     * @return
     */
    @Bean
    public KeycloakConfigResolver keycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }

    @Autowired
    public void configureProvider(AuthenticationManagerBuilder builder) {
        builder.authenticationProvider(keycloakAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        // Configurations personnalisées
        http
                // delegate logout endpoint to spring security
                .logout().addLogoutHandler(keycloakLogoutHandler()).logoutUrl("/logout").logoutSuccessUrl("/").and()
                // regles de securites
                // tout en https
                .requiresChannel().anyRequest().requiresSecure().and()
                // espace public obligatoire pour consulter swagger, fonctionne si swagger
                // deployer a la racine
                .authorizeRequests()
                .antMatchers("/application/{\\d+}/deploy", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                .permitAll()
                // Domaine public
                .and().authorizeRequests().antMatchers("/api/public/**").permitAll()
                // Domaine privé
                .antMatchers("/**").authenticated();
    }

}
