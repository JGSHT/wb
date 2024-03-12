package org.example.security.multiple.converter;

import org.example.security.multiple.authentication.SmsAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.function.Function;

public class MultipleAuthConverter implements Function<ServerWebExchange, Mono<Authentication>> {

    private String grantTypeParameter = "grantType";

    private String usernameParameter = "username";

    private String passwordParameter = "password";

    private String phoneParameter = "phone";

    private String smsCodeParameter = "smsCode";
    @Override
    public Mono<Authentication> apply(ServerWebExchange exchange) {
        return exchange.getFormData().map(this::createAuthentication);
    }

    private Authentication createAuthentication(MultiValueMap<String, String> data){
        String grantType = data.getFirst(this.grantTypeParameter);
        if (grantType.equals("sms")){
            String phone = data.getFirst(this.phoneParameter);
            String smsCode = data.getFirst(this.smsCodeParameter);
            return new SmsAuthenticationToken(phone, smsCode, false, new ArrayList<>());
        }
        String username = data.getFirst(this.usernameParameter);
        String password = data.getFirst(this.passwordParameter);
        return UsernamePasswordAuthenticationToken.unauthenticated(username, password);
    }
}
