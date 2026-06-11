package com.sip.book_shop.security.authentication;

import com.sip.book_shop.common.utils.ObjectUtils;
import com.sip.book_shop.repositories.UserRepository;
import com.sip.book_shop.security.authentication.userdetail.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class AuthProvider {

    @Value("${jwt.secret}")
    private String SECRET;

    @Value("${jwt.access_token.expire}")
    private long ACCESS_TOKEN_EXPIRE;

    @Value("${jwt.refresh_token.expire}")
    private long REFRESH_TOKEN_EXPIRE;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

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

    private String buildToken(
            Map<String, Object> claims,
            String username,
            long expiration
    ) {
        return Jwts.builder()
                .addClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public UserDetails getPrincipalFromToken(String token) {
        Claims claims = extractAllClaims(token);
        String principal = claims.get("principal", String.class);
        if (principal == null) {
            throw new IllegalArgumentException("Invalid token: Principal data is missing.");
        }

        return userDetailsServiceImpl.loadUserByUsername(claims.getSubject());
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
