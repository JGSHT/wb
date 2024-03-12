package org.example.security.jwt;


import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.security.model.UserDetail;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class JwtService implements ReactiveJwtDecoder {

    private final JwtProperties jwtProperties;

    private final static String BEARER = "Bearer ";
    private static final String EXP_KEY = "expired_at";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private JwtParser jwtParser;

    public JwtService(JwtProperties jwtProperties){
        this.jwtProperties = jwtProperties;
        this.jwtParser = createJwtParser();
    }




    private JwtParser createJwtParser(){
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecret()))).build();
    }


    public Claims getAllClaimsFromToken(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public Map<String, Object> parseJWT(String jwtString) {
        Jws<Claims> claimsJws = doParse(jwtString);
        return claimsJws != null ? claimsJws.getPayload() : Map.of();
    }

    private Jws<Claims> doParse(String jwtString){
        if (jwtString.startsWith(BEARER)){
            jwtString = jwtString.substring(BEARER.length());
        }
        return jwtParser.parse(jwtString).accept(Jws.CLAIMS);
    }

    public String getUsernameFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    public Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(UserDetail user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", JSONObject.toJSONString(user.getRoles()));
        return doGenerateToken(claims, user.getUsername());
    }

    private String doGenerateToken(Map<String, Object> claims, String username) {
        final Date createdDate = new Date();
        final Date expirationDate = new Date(createdDate.getTime() + jwtProperties.getExpireTime() * 1000);
        LocalDateTime expireTime = LocalDateTime.now().plusSeconds(jwtProperties.getExpireTime());
        claims.put(EXP_KEY, expireTime.format(FORMATTER));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecret())))
                .compact();
    }

    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    @Override
    public Mono<Jwt> decode(String token) throws JwtException {
        Jwt build = null;
        token = token.startsWith(BEARER) ? token.substring(BEARER.length()) : token;
        Jws<Claims> claimsJws = doParse(token);
        Claims payload = claimsJws.getPayload();
        String expKey = payload.get(EXP_KEY, String.class);
        LocalDateTime expire = LocalDateTime.parse(expKey, FORMATTER);
        build = Jwt.withTokenValue(token)
                .headers(headers -> headers.putAll(claimsJws.getHeader()))
                .claims(claims -> claims.putAll(claimsJws.getBody()))
                .expiresAt(expire.atZone(ZoneId.systemDefault()).toInstant())
                .issuedAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())
                .build();
        return Mono.just(build);
    }
}
