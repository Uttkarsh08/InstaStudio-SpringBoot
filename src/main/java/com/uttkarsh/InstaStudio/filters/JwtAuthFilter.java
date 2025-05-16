package com.uttkarsh.InstaStudio.filters;

import com.uttkarsh.InstaStudio.exceptions.UnregisteredUserException;
import com.uttkarsh.InstaStudio.services.JwtService;
import com.uttkarsh.InstaStudio.services.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.util.List;

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

            String token = requestHeaderToken.split("Bearer ")[1];
            Claims claims = jwtService.getAllClaims(token);

            String firebaseUid = claims.getSubject();
            String userTypeStr = claims.get("userType", String.class);
            if (userTypeStr == null || userTypeStr.isBlank()) {
                throw new AccessDeniedException("Missing or invalid user type in token");
            }

            Boolean isRegistered = claims.get("isRegistered", Boolean.class);

            if (Boolean.FALSE.equals(isRegistered)) {
                if (!request.getRequestURI().startsWith("/api/v1/register/user") &&
                        !request.getRequestURI().startsWith("/api/auth")) {
                    throw new UnregisteredUserException("User must complete profile before accessing protected resources");
                }
            }

            var authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_"+userTypeStr)
            );


            var auth = new UsernamePasswordAuthenticationToken(firebaseUid, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }

    }
}
