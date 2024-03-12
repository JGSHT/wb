package org.example.security.handler;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import org.example.core.R;
import org.example.security.jwt.JwtService;
import org.example.security.model.UserDetail;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.WebFilterChainServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class AuthenticationSuccessHandler extends WebFilterChainServerAuthenticationSuccessHandler {

    private final JwtService jwtService;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetail);
        ServerWebExchange exchange = webFilterExchange.getExchange();
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().add("Content-Type", "application/json; charset=UTF-8");
        String jsonString = JSONObject.toJSONString(R.success(token));
        return response.writeWith(Mono.just(response.bufferFactory().wrap(jsonString.getBytes())));
    }
}
