package com.example.uberprojectauthservice.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService implements CommandLineRunner {
    @Value("${jwt.expiry}")
    private int expiry;

    @Value("${jwt.secret}")
    private String SECRET;

    /**
     * This method creates a brand new JWT token for us based on payload
     */
    private String createToken(Map<String, Object> payload, String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiry*1000L);

        return Jwts.builder()
                .claims(payload)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(expiryDate)
                .subject(email)
                .signWith(getSignKey())
                .compact();
    };

    private Claims extractAllPayloads(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())   // 🔑 REQUIRED in 0.13.x
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllPayloads(token);
        return claimsResolver.apply(claims);
    }

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    private String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private boolean validateToken(String token, String email) {
        final String userEmailFetchedFromToken = extractEmail(token);
        return email.equals(userEmailFetchedFromToken) && !isTokenExpired(token);
    }
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * This method checks if the token expiry was before the current time stamp or not?
     * @param token JWT token
     * @return true if token is expired else false
     */
    private boolean isTokenExpired(String token) {
        return  extractExpiration(token).before(new Date());
    }

    private Object extractPayload(String token, String payloadKey) {
        Claims claim = extractAllPayloads(token);
        return claim.get(payloadKey);
    }

    @Override
    public void run(String... args) throws Exception {
        Map<String,Object> mp = new HashMap<>();
        mp.put("email", "a@b.com");
        mp.put("phoneNumber", "123456789");
        String result = createToken(mp, "user");
        System.out.println("Generated token : " + result);
        System.out.println(extractPayload(result, "email"));
    }
}
