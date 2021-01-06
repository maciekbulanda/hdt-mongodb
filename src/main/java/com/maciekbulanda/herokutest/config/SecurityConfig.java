package com.maciekbulanda.herokutest.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.Date;

import static org.springframework.web.cors.CorsConfiguration.ALL;

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

        http.cors(corsSpec -> {
            UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
            CorsConfiguration corsConfiguration = new CorsConfiguration();
            corsConfiguration.addAllowedOrigin("http://localhost:3000");
            corsConfiguration.addAllowedMethod(HttpMethod.OPTIONS);
            corsConfiguration.addAllowedMethod(HttpMethod.GET);
            corsConfiguration.addAllowedMethod(HttpMethod.POST);
            corsConfiguration.addAllowedMethod(HttpMethod.PUT);
            corsConfiguration.addAllowedMethod(HttpMethod.DELETE);
            corsConfiguration.addAllowedHeader("*");
            corsConfiguration.addExposedHeader("Authorization");
            configurationSource.registerCorsConfiguration("/**", corsConfiguration);
            corsSpec.configurationSource(configurationSource);
        });

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
        } catch (JOSEException e) {
            e.printStackTrace();
        }
        return "Bearer " + signedJWT.serialize();
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
