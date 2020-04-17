package com.example.demo.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@ConditionalOnProperty(name = "com.example.demo.auth.type", havingValue = "nosecurity")
public class NoSecurity extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // tout en https
        http.requiresChannel().anyRequest().requiresSecure().and().authorizeRequests().antMatchers("/**").permitAll();
        http.headers().frameOptions().disable();
        http.csrf().disable();
    }
}