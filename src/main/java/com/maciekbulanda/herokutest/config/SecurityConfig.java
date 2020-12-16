package com.maciekbulanda.herokutest.config;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@EnableWebFluxSecurity
public class SecurityConfig {

    @Value(value = "${params.secret}")
    private String secret;

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService());

        CustomAuthenticationWebFilter wf = new CustomAuthenticationWebFilter(authenticationManager);
        wf.setAuthenticationSuccessHandler((webFilterExchange, authentication) -> {
            webFilterExchange.getExchange().getResponse().getHeaders().add("Authorization", generateToken(authentication));
            return Mono.empty();
        });


        http
                .authorizeExchange(exchangeSpec -> exchangeSpec.pathMatchers(HttpMethod.GET, "/login").authenticated())
                .addFilterAt(wf, SecurityWebFiltersOrder.HTTP_BASIC)
                .authorizeExchange(exchangeSpec -> exchangeSpec.pathMatchers("/api/**").authenticated())
                .authorizeExchange(exchangeSpec -> exchangeSpec.pathMatchers("/").permitAll());

        http.cors().disable();

        return http.build();
    }

    private String generateToken(Authentication authentication) {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .issuer("MB")
                    .subject(authentication.getName())
                    .expirationTime(new Date())
                    .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        try {
            JWSSigner jwsSigner = new MACSigner(secret);
            signedJWT.sign(jwsSigner);
        }
        catch (JOSEException e) {
            e.printStackTrace();
        }
        return "Bearer " +signedJWT.serialize();
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
