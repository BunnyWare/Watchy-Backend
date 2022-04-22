package de.fayedev.watchybackend.security;

import de.fayedev.watchybackend.exception.ApplicationException;
import de.fayedev.watchybackend.exception.ApplicationExceptionCode;
import de.fayedev.watchybackend.model.user.Role;
import de.fayedev.watchybackend.repo.UserRepository;
import de.fayedev.watchybackend.utils.LogMessage;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {

    private final UserRepository userRepository;

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.validity}")
    private long validityInSeconds;

    public JwtTokenProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String username, List<Role> roles) {
        var claims = Jwts.claims().setSubject(username);
        claims.put("auth", roles.stream().map(s -> new SimpleGrantedAuthority(s.getAuthority())).collect(Collectors.toList()));

        var now = new Date();
        // JWT needs to be converted to milliseconds.
        var validity = new Date(now.getTime() + (validityInSeconds * 1000));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    @Transactional
    public Authentication getAuthentication(String token) throws ApplicationException {
        String username = getUserName(token);
        final var user = userRepository.findByUsername(username).orElseThrow(() -> {
            log.info(LogMessage.USER_NOT_FOUND, username);
            return new ApplicationException(HttpStatus.NOT_FOUND, ApplicationExceptionCode.USER_NOT_FOUND, MessageFormatter.format(LogMessage.USER_NOT_FOUND, username).getMessage());
        });

        // This sets the authentication name also for WebSockets!
        return new UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getRoles());
    }

    private String getUserName(String token) {
        return Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes())).build().parseClaimsJws(token).getBody().getSubject();
    }

    String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        } else if (req.getParameter("token") != null) { // For websockets, which do not support headers.
            return req.getParameter("token");
        }

        return null;
    }

    public boolean validateToken(String token) throws ApplicationException {
        try {
            Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes())).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, ApplicationExceptionCode.JWT_INVALID_OR_EXPIRED, MessageFormatter.format(LogMessage.JWT_INVALID_OR_EXPIRED, token).getMessage());
        }
    }
}
