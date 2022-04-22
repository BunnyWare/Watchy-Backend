package de.fayedev.watchybackend.config;

import de.fayedev.watchybackend.security.JwtTokenFilterConfigurer;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenFilterConfigurer jwtTokenFilterConfigurer;

    public SecurityConfig(JwtTokenFilterConfigurer jwtTokenFilterConfigurer) {
        this.jwtTokenFilterConfigurer = jwtTokenFilterConfigurer;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http
                .authorizeRequests()
                .antMatchers("/api/users/register")
                .permitAll()
                .antMatchers("/api/users/login")
                .permitAll()
                .antMatchers("/api/users/confirm")
                .permitAll()
                .antMatchers("/api/users/resendConfirm")
                .permitAll()
                .antMatchers("/socket/**")
                .permitAll()
                .antMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                .permitAll()
                .and()
                .authorizeRequests()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .permitAll()
                .anyRequest()
                .authenticated();

        http.apply(jwtTokenFilterConfigurer);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        var configuration = new CorsConfiguration();
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
