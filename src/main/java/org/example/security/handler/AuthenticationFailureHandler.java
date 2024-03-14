package org.example.security.handler;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import org.example.core.R;
import org.example.security.exception.SmsTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class AuthenticationFailureHandler implements ServerAuthenticationFailureHandler {


    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        ServerWebExchange exchange = webFilterExchange.getExchange();
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json; charset=UTF-8");
        String message = "账号密码错误！";
        if (exception instanceof SmsTokenException){
            message = exception.getMessage();
        }
        String jsonString = JSONObject.toJSONString(R.fail(message));
        return response.writeWith(Mono.just(response.bufferFactory().wrap(jsonString.getBytes())));
    }
}
