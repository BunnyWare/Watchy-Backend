package de.fayedev.watchybackend.security;

import de.fayedev.watchybackend.exception.ApplicationException;
import de.fayedev.watchybackend.utils.LogMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(httpServletRequest);
        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                var auth = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (ApplicationException e) {
            log.info(LogMessage.JWT_AUTHENTICATION_ERROR, e);
            // Clears user from authentication, very important
            SecurityContextHolder.clearContext();
            httpServletResponse.sendError(e.getHttpStatus().value());
            return;
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
