package org.example.security.jwt;


import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import org.example.entity.Role;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

import static org.example.security.exception.JwtTokenException.expectNotNull;

@AllArgsConstructor
public class JwtTokenConvertor implements Converter<Jwt, Mono<JwtAuthenticationToken>> {


    @Override
    public Mono<JwtAuthenticationToken> convert(Jwt source) {
        return Mono.just(source).map(jwt -> {
            expectNotNull(jwt, "登陆过期");
            return source.getClaims();
        }).flatMap(claims -> {
            return Mono.just(new JwtAuthenticationToken(source,
                    JSONObject.parseArray(claims.get("role").toString(), Role.class)
                                    .stream().map(role -> "ROLE_" + role.getRoleCode())
                                    .map(SimpleGrantedAuthority::new).collect(Collectors.toSet())));
//            List<Role> roles = (List<Role>)claims.get("role");
//            Set<String> authorities = roles.stream().map(Role::getRoleCode).collect(Collectors.toSet());
//            JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(source,
//                    authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet()));
//            jwtAuthenticationToken.setAuthenticated(false);
//            jwtAuthenticationToken.setDetails(source.getSubject());
//            return Mono.just(jwtAuthenticationToken);
        });
    }
}
