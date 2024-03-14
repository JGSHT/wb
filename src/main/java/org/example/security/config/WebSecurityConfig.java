package org.example.security.config;


import org.example.security.handler.AuthenticationFailureHandler;
import org.example.security.handler.AuthenticationSuccessHandler;
import org.example.security.jwt.JwtProperties;
import org.example.security.jwt.JwtService;
import org.example.security.jwt.JwtTokenConvertor;
import org.example.security.manager.OAuthorizationManager;
import org.example.security.multiple.converter.MultipleAuthConverter;
import org.example.security.multiple.filter.OAuthenticationFilter;
import org.example.security.multiple.manager.SmsReactiveAuthenticationManager;
import org.example.service.SmsService;
import org.example.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.DelegatingReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.LinkedList;

@EnableWebFluxSecurity
@Configuration
public class WebSecurityConfig {

    @Bean
    public JwtService jwtService(JwtProperties jwtProperties){
        return new JwtService(jwtProperties);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(
            UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder,
            SmsService smsService) {
        LinkedList<ReactiveAuthenticationManager> managers = new LinkedList<>();
        managers.add(new SmsReactiveAuthenticationManager(smsService, userService));
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager
                = new UserDetailsRepositoryReactiveAuthenticationManager(userService);
        authenticationManager.setPasswordEncoder(bCryptPasswordEncoder);
        managers.add(authenticationManager);
        return new DelegatingReactiveAuthenticationManager(managers);
    }

    @Bean
    public OAuthenticationFilter oAuthenticationFilter(ReactiveAuthenticationManager authenticationManager,
                                                       AuthenticationSuccessHandler authenticationSuccessHandler,
                                                       AuthenticationFailureHandler authenticationFailureHandler){
        OAuthenticationFilter oAuthenticationFilter = new OAuthenticationFilter(authenticationManager);
        oAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        oAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        oAuthenticationFilter.setServerAuthenticationConverter(new MultipleAuthConverter());
        oAuthenticationFilter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());
        oAuthenticationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/aa/login"));
        return oAuthenticationFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
            ReactiveAuthenticationManager reactiveAuthenticationManager,
            AuthenticationSuccessHandler authenticationSuccessHandler,
            AuthenticationFailureHandler authenticationFailureHandler,
            OAuthorizationManager oAuthorizationManager,
            OAuthenticationFilter oAuthenticationFilter){
        return http
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(formLoginSpec ->
                        formLoginSpec
                                .loginPage("/auth/login")
                                .authenticationSuccessHandler(authenticationSuccessHandler)
                                .authenticationFailureHandler(authenticationFailureHandler)
                )
                .addFilterAfter(oAuthenticationFilter, SecurityWebFiltersOrder.FORM_LOGIN)
                .oauth2ResourceServer(oauth2 ->
                        oauth2.bearerTokenConverter(this::MatchRequest)
                                .jwt(jwtSpec -> jwtSpec.jwtAuthenticationConverter(new JwtTokenConvertor())))
                .authenticationManager(reactiveAuthenticationManager)
                .exceptionHandling(exceptionHandlingSpec ->
                        exceptionHandlingSpec.authenticationEntryPoint((serverWebExchange, e) ->
                                Mono.fromRunnable(() -> serverWebExchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED))
                        ).accessDeniedHandler((serverWebExchange, e) ->
                                Mono.fromRunnable(() -> serverWebExchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN))
                        ))
                .authorizeExchange(authorizeExchangeSpec -> {
                    authorizeExchangeSpec.pathMatchers("/auth/login").permitAll();
                    authorizeExchangeSpec.pathMatchers("/aa/login").permitAll();
                    authorizeExchangeSpec.anyExchange().access(oAuthorizationManager);
                })
                .build();
    }


    public Mono<Authentication> MatchRequest(ServerWebExchange serverWebExchange){
        return Mono.justOrEmpty(serverWebExchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .filter(authHeader -> authHeader.startsWith("Bearer "))
                .flatMap(authHeader -> Mono.just(new BearerTokenAuthenticationToken(authHeader)));
    }
}
