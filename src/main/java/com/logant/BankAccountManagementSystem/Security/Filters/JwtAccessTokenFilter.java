package com.logant.BankAccountManagementSystem.Security.Filters;


import com.logant.BankAccountManagementSystem.Security.Enum.TokenType;
import com.logant.BankAccountManagementSystem.Security.config.RSAKeyRecord;
import com.logant.BankAccountManagementSystem.Security.config.jwtAuth.JwtTokenUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAccessTokenFilter extends OncePerRequestFilter {

    private final RSAKeyRecord rsaKeyRecord;
    private final JwtTokenUtils jwtTokenUtils;

    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            log.info("[JwtAccessTokenFilter:doFilterInternal] :: Started ");

            log.info("[JwtAccessTokenFilter:doFilterInternal]Filtering the Http Request:{}", request.getRequestURI());

            final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            // Check if authHeader is null or empty before proceeding
            if (authHeader == null || authHeader.isBlank()) {
                setErrorResponse(response, HttpStatus.UNAUTHORIZED, "Authorization header is missing or empty");
                return;
            }

            if (!authHeader.startsWith(TokenType.Bearer.name())) {
                setErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid Token Type");
                return;
            }

            // Extract token and check for null or empty
            final String token = authHeader.substring(7);
            if (token.isEmpty()) {
                setErrorResponse(response, HttpStatus.UNAUTHORIZED, "Token is empty");
                return;
            }

            JwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(rsaKeyRecord.rsaPublicKey()).build();
            final Jwt jwtToken = jwtDecoder.decode(token);

            if (jwtTokenUtils.getIfTokenIsExpired(jwtToken)) {
                setErrorResponse(response, HttpStatus.UNAUTHORIZED, "Token is expired");
                return;
            }

            final String userName = jwtTokenUtils.getUserName(jwtToken);

            // Check if userName is null or empty
            if (userName == null || userName.isEmpty()) {
                setErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid user in token");
                return;
            }

            // Check if the SecurityContext already has authentication
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = jwtTokenUtils.userDetails(userName);
                if (jwtTokenUtils.isTokenValid(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken createdToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    createdToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                    securityContext.setAuthentication(createdToken);
                    SecurityContextHolder.setContext(securityContext);
                }
            }

            log.info("[JwtAccessTokenFilter:doFilterInternal] Completed");

            // Proceed with the filter chain
            filterChain.doFilter(request, response);

        } catch (JwtValidationException jwtValidationException) {
            if (jwtValidationException.getMessage().contains("Jwt expired")) {
                log.error("[JwtAccessTokenFilter:doFilterInternal] Token expired: {}", jwtValidationException.getMessage());
                setErrorResponse(response, HttpStatus.UNAUTHORIZED, "Token is expired. Please refresh your token.");
            } else {
                log.error("[JwtAccessTokenFilter:doFilterInternal] JWT Validation Exception: {}", jwtValidationException.getMessage());
                setErrorResponse(response, HttpStatus.BAD_REQUEST, "Invalid JWT token");
            }
        } catch (NullPointerException ex) {
            log.error("[JwtAccessTokenFilter:doFilterInternal] NullPointerException: {}", ex.getMessage());
            setErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "A null pointer exception occurred.");
        } catch (Exception ex) {
            log.error("[JwtAccessTokenFilter:doFilterInternal] Exception: {}", ex.getMessage());
            setErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while processing the token.");
        }
    }

    private void setErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE,
                "Bearer");
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
