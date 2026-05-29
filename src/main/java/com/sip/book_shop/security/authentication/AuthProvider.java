package com.sip.book_shop.security.authentication;

import com.sip.book_shop.common.utils.ObjectUtils;
import com.sip.book_shop.repositories.UserRepository;
import com.sip.book_shop.security.authentication.userdetail.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
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

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    public String generateToken(Authentication authentication) {
        String principal = ObjectUtils.serializeToJson(authentication.getPrincipal());

        Map<String, Object> claims = new HashMap<>();
        claims.put("principal", principal);

        return Jwts.builder()
                .addClaims(claims)
                .setSubject(authentication.getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
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
