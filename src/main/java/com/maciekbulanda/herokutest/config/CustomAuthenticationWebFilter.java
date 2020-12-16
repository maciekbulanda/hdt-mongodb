package com.maciekbulanda.herokutest.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManagerResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.*;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public class CustomAuthenticationWebFilter implements WebFilter {

    private static final Log logger = LogFactory.getLog(AuthenticationWebFilter.class);

    private final ReactiveAuthenticationManagerResolver<ServerWebExchange> authenticationManagerResolver;

    private ServerAuthenticationSuccessHandler authenticationSuccessHandler = new WebFilterChainServerAuthenticationSuccessHandler();

    private ServerAuthenticationConverter authenticationConverter = new ServerHttpBasicAuthenticationConverter();

    private ServerAuthenticationFailureHandler authenticationFailureHandler = new ServerAuthenticationEntryPointFailureHandler(
            new HttpBasicServerAuthenticationEntryPoint());

    private ServerSecurityContextRepository securityContextRepository = NoOpServerSecurityContextRepository
            .getInstance();

    private ServerWebExchangeMatcher requiresAuthenticationMatcher = ServerWebExchangeMatchers.anyExchange();

    public CustomAuthenticationWebFilter(ReactiveAuthenticationManager authenticationManager) {
        Assert.notNull(authenticationManager, "authenticationManager cannot be null");
        this.authenticationManagerResolver = (request) -> Mono.just(authenticationManager);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return this.requiresAuthenticationMatcher.matches(exchange).filter((matchResult) -> matchResult.isMatch())
                .flatMap((matchResult) -> this.authenticationConverter.convert(exchange))
                .switchIfEmpty(chain.filter(exchange).then(Mono.empty()))
                .flatMap((token) -> authenticate(exchange, chain, token))
                .onErrorResume(AuthenticationException.class, (ex) -> this.authenticationFailureHandler
                        .onAuthenticationFailure(new WebFilterExchange(exchange, chain), ex));
    }

    private Mono<Void> authenticate(ServerWebExchange exchange, WebFilterChain chain, Authentication token) {
        return this.authenticationManagerResolver.resolve(exchange)
                .flatMap((authenticationManager) -> authenticationManager.authenticate(token))
                .switchIfEmpty(Mono.defer(
                        () -> Mono.error(new IllegalStateException("No provider found for " + token.getClass()))))
                .flatMap((authentication) -> onAuthenticationSuccess(authentication,
                        new WebFilterExchange(exchange, chain)))
                .doOnError(AuthenticationException.class,
                        (ex) -> logger.debug(LogMessage.format("Authentication failed: %s", ex.getMessage())));
    }

    protected Mono<Void> onAuthenticationSuccess(Authentication authentication, WebFilterExchange webFilterExchange) {
        ServerWebExchange exchange = webFilterExchange.getExchange();
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);
        return this.securityContextRepository.save(exchange, securityContext)
                .then(this.authenticationSuccessHandler.onAuthenticationSuccess(webFilterExchange, authentication))
                .subscriberContext(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
    }

    public void setAuthenticationSuccessHandler(ServerAuthenticationSuccessHandler authenticationSuccessHandler) {
        Assert.notNull(authenticationSuccessHandler, "authenticationSuccessHandler cannot be null");
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    /**
     * Sets the strategy used for converting from a {@link ServerWebExchange} to an
     * {@link Authentication} used for authenticating with the provided
     * {@link ReactiveAuthenticationManager}. If the result is empty, then it signals that
     * no authentication attempt should be made. The default converter is
     * {@link ServerHttpBasicAuthenticationConverter}
     * @param authenticationConverter the converter to use
     * @deprecated As of 5.1 in favor of
     * {@link #setServerAuthenticationConverter(ServerAuthenticationConverter)}
     * @see #setServerAuthenticationConverter(ServerAuthenticationConverter)
     */
    @Deprecated
    public void setAuthenticationConverter(Function<ServerWebExchange, Mono<Authentication>> authenticationConverter) {
        Assert.notNull(authenticationConverter, "authenticationConverter cannot be null");
        setServerAuthenticationConverter(authenticationConverter::apply);
    }

    /**
     * Sets the strategy used for converting from a {@link ServerWebExchange} to an
     * {@link Authentication} used for authenticating with the provided
     * {@link ReactiveAuthenticationManager}. If the result is empty, then it signals that
     * no authentication attempt should be made. The default converter is
     * {@link ServerHttpBasicAuthenticationConverter}
     * @param authenticationConverter the converter to use
     * @since 5.1
     */
    public void setServerAuthenticationConverter(ServerAuthenticationConverter authenticationConverter) {
        Assert.notNull(authenticationConverter, "authenticationConverter cannot be null");
        this.authenticationConverter = authenticationConverter;
    }

    /**
     * Sets the failure handler used when authentication fails. The default is to prompt
     * for basic authentication.
     * @param authenticationFailureHandler the handler to use. Cannot be null.
     */
    public void setAuthenticationFailureHandler(ServerAuthenticationFailureHandler authenticationFailureHandler) {
        Assert.notNull(authenticationFailureHandler, "authenticationFailureHandler cannot be null");
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    /**
     * Sets the matcher used to determine when creating an {@link Authentication} from
     * {@link #setServerAuthenticationConverter(ServerAuthenticationConverter)} to be
     * authentication. If the converter returns an empty result, then no authentication is
     * attempted. The default is any request
     * @param requiresAuthenticationMatcher the matcher to use. Cannot be null.
     */
    public void setRequiresAuthenticationMatcher(ServerWebExchangeMatcher requiresAuthenticationMatcher) {
        Assert.notNull(requiresAuthenticationMatcher, "requiresAuthenticationMatcher cannot be null");
        this.requiresAuthenticationMatcher = requiresAuthenticationMatcher;
    }

}