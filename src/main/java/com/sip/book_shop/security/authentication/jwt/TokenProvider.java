package com.sip.book_shop.security.authentication.jwt;

import com.sip.book_shop.common.utils.ObjectUtils;
import com.sip.book_shop.security.dto.CustomUserDetails;
import com.sip.book_shop.security.service.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    @Value("${jwt.secret}")
    private String SECRET;

    @Value("${jwt.token.access.expire-time}")
    private long ACCESS_TOKEN_EXPIRE;

    @Value("${jwt.token.refresh.expire-time}")
    private long REFRESH_TOKEN_EXPIRE;

    private final CustomUserDetailsService customUserDetailsService;

    public TokenResponse createToken(Authentication authentication) {
        String accessToken = generateAccessToken(authentication);
        String refreshToken = generateRefreshToken(authentication);
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, ACCESS_TOKEN_EXPIRE);
    }

    public String generateRefreshToken(Authentication authentication) {
        return generateToken(authentication, REFRESH_TOKEN_EXPIRE);
    }

    public String generateAccessTokenFromUserDetails(UserDetails userDetails) {
        String principal = ObjectUtils.serializeToJson(userDetails);
        Map<String, Object> claims = new HashMap<>();
        claims.put("principal", principal);
        return buildToken(claims, userDetails.getUsername(), ACCESS_TOKEN_EXPIRE);
    }

    public String generateToken(Authentication authentication, long expiration) {
        String principal = ObjectUtils.serializeToJson(authentication.getPrincipal());
        Map<String, Object> claims = new HashMap<>();
        claims.put("principal", principal);
        return buildToken(claims, authentication.getName(), expiration);
    }

    private String buildToken(Map<String, Object> claims, String username, long expirationInMinutes) {
        long expirationInMillis = TimeUnit.MINUTES.toMillis(expirationInMinutes);
        return Jwts.builder()
                .addClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationInMillis))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            return false;
        }
    }

    public CustomUserDetails getPrincipalFromToken(String token) {
        Claims claims = extractAllClaims(token);
        String principal = claims.get("principal", String.class);
        if (principal == null) {
            throw new IllegalArgumentException("Invalid token: Principal data is missing.");
        }

        return customUserDetailsService.loadUserByUsername(claims.getSubject());
    }

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
