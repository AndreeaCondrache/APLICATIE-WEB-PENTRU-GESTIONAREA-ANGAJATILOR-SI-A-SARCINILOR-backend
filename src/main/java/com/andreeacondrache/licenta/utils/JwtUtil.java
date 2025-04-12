package com.andreeacondrache.licenta.utils;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import  io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private Key getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode("413F4428472B4B6250655368566D59703373367639792442264529484404D6351");
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private String generateToken(Map<String, Object> extraClaims, UserDetails  userDetails){
        return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000 * 60 * 60 * 24))
                .signWith(getSigningKey(),SignatureAlgorithm.HS256)
                .compact();
    }


    public String generateToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .toList();
        extraClaims.put("authorities", roles);

        String token = generateToken(extraClaims, userDetails); // <-- generează tokenul
        System.out.println("✅ Token generat în backend: " + token); // <-- abia acum îl poți afișa
        return token;
    }


    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
     }


    private <T> T extractClaims(String token, Function<Claims, T> claimsResolvers){
       final Claims claims = extractAllClaims(token);
       return claimsResolvers.apply(claims);
     }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

     public String extractUsername(String token){
        return extractClaims(token, Claims::getSubject);
     }


    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }


    private Date extractExpiration(String token){
        return extractClaims(token , Claims::getExpiration);

     }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}











