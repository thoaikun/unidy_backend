package com.unidy.backend.config;

import com.google.gson.Gson;
import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.repositories.AdminRepository;
import com.unidy.backend.repositories.AdminTokenRepository;
import com.unidy.backend.repositories.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private final TokenRepository tokenRepository;
  private final AdminTokenRepository adminTokenRepository;
  private final AdminRepository adminRepository;
  private final UserDetailsService userDetailsServiceAdmin;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain
  )  throws ServletException, IOException, AccessDeniedException {
    try {
      if (request.getServletPath().contains("/api/v1/auth") || request.getServletPath().contains("/api/v1/admin/auth")) {
        filterChain.doFilter(request, response);
        return;
      }

      final String authHeader = request.getHeader("Authorization");

      final String jwt;
      final String userEmail;
      if (request.getServletPath().contains("/api/v1/admin")) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
          filterChain.doFilter(request, response);
          return;
        }
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

          UserDetails userDetails = this.userDetailsServiceAdmin.loadUserByUsername(userEmail);

          var isTokenValid = adminTokenRepository.findByToken(jwt)
                  .map(t -> !t.isExpired() && !t.isRevoked())
                  .orElse(false);
          if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);
          }
        }
        filterChain.doFilter(request, response);
      } else {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
          filterChain.doFilter(request, response);
          return;
        }
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
          UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
          var isTokenValid = tokenRepository.findByToken(jwt)
                  .map(t -> !t.isExpired() && !t.isRevoked())
                  .orElse(false);
          if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);
          }
        }
        filterChain.doFilter(request, response);
      }
    } catch (AccessDeniedException e) {
      response.setStatus(HttpStatus.FORBIDDEN.value());
      response.setCharacterEncoding("UTF-8");
      response.getWriter().write(new Gson().toJson(new ErrorResponseDto(e.getMessage())));
    }
  }
}
