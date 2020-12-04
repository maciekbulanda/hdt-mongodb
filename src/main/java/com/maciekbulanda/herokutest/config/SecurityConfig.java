package com.maciekbulanda.herokutest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.web.server.WebFilter;

@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService());

        WebFilter wf = new AuthenticationWebFilter(authenticationManager);

        http
                .httpBasic()
                .and()
                .authorizeExchange(exchangeSpec -> exchangeSpec.pathMatchers(HttpMethod.GET, "/login").authenticated())
                .addFilterAt(wf, SecurityWebFiltersOrder.HTTP_BASIC)
                .authorizeExchange(exchangeSpec -> exchangeSpec.pathMatchers("/api/**").authenticated())
                .authorizeExchange(exchangeSpec -> exchangeSpec.pathMatchers("/").permitAll());

        http.cors().disable();

        return http.build();
    }

    @Bean
    ReactiveUserDetailsService userDetailsService() {

        var user = User
                .withUsername("user")
                .password("{noop}user")
                .authorities("user")
                .build();

        return new MapReactiveUserDetailsService(user);
    }

}
