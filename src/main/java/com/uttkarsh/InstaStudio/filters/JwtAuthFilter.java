package com.uttkarsh.InstaStudio.filters;

import com.uttkarsh.InstaStudio.services.JwtService;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try{
            final String requestHeaderToken = request.getHeader("Authorization");
            if(requestHeaderToken == null || !requestHeaderToken.startsWith("Bearer ")){
                filterChain.doFilter(request, response);
                return;
            }

            String token =requestHeaderToken.split("Bearer ")[1];
            String firebaseUid = jwtService.getFireBaseIdFromToken(token);

            var auth = new UsernamePasswordAuthenticationToken(firebaseUid, null, null);
            SecurityContextHolder.getContext().setAuthentication(auth);



            filterChain.doFilter(request, response);

        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }

    }
}
