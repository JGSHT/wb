package org.example.security.manager;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.repository.RoleMenuRepository;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class OAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private final RoleMenuRepository roleMenuRepository;


    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
        return authentication.flatMap(auth -> {
                return roleMenuRepository.getAllRoleMenus()
                    .filterWhen(menuRolesDto ->
                            new PathPatternParserServerWebExchangeMatcher(menuRolesDto.getPath())
                                    .matches(authorizationContext.getExchange())
                                    .map(ServerWebExchangeMatcher.MatchResult::isMatch)
                    ).next()
                    .map(menuRolesDto -> {
                        if (hasAnyRole(auth.getAuthorities().stream()
                                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()),
                                Arrays.stream(menuRolesDto.getRoleCodes().split(","))
                                        .map(o -> "ROLE_" + o).toList()) ) {
                            return new AuthorizationDecision(true);
                        }
                            return new AuthorizationDecision(false);
                    });
        }).defaultIfEmpty(new AuthorizationDecision(false));
    }

    private boolean hasAnyRole(List<String> authorities, List<String> roles){
        Set<String> merge = new HashSet<>();
        merge.addAll(authorities);
        merge.addAll(roles);
        return merge.size() < authorities.size() + roles.size();
    }
}