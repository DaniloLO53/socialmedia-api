package org.xuxuchat.app.security.config;

import org.xuxuchat.app.security.exceptions.CustomAuthEntryPoint;
import org.xuxuchat.app.security.exceptions.ExceptionHandlerFilter;
import org.xuxuchat.app.security.jwt.AuthTokenJwtFilter;
import org.xuxuchat.app.security.services.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final CustomAuthEntryPoint customAuthEntryPoint;
    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(CustomAuthEntryPoint customAuthEntryPoint, UserDetailsServiceImpl userDetailsService) {
        this.customAuthEntryPoint = customAuthEntryPoint;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain authSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        http.headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        http.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.exceptionHandling(e -> e.authenticationEntryPoint(customAuthEntryPoint));

        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/api/auth/**").anonymous();

            // Documentation
            auth.requestMatchers(
                    "/favicon.ico",
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**"
            ).permitAll();

            // WebSockets
            auth.requestMatchers("/ws/**").permitAll();

            // Public endpoints
            auth.requestMatchers("/api/public/**").permitAll();
            auth.requestMatchers("/error").permitAll();

            auth.requestMatchers(
                    "/api/test/**",
                    "/images/**",
                    "/h2-console/**",
                    "/webjars/**"
            ).permitAll();

            auth.anyRequest().authenticated();
        });

        http.authenticationProvider(daoAuthenticationProvider());

        http.addFilterBefore(authTokenJwtFilterBean(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(exceptionHandlerFilterBean(), AuthTokenJwtFilter.class);

        return http.build();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 1)
    public AuthTokenJwtFilter authTokenJwtFilterBean() {
        return new AuthTokenJwtFilter();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ExceptionHandlerFilter exceptionHandlerFilterBean() {
        return new ExceptionHandlerFilter();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Defina a origem do seu frontend (React rodando na porta 5173, por exemplo)
        // NUNCA use "*" em produção se você usa cookies ou autenticação baseada em sessão!
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://127.0.0.1:5173"));

        // Defina os métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Defina os cabeçalhos permitidos (essencial para headers customizados como Authorization)
//        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowedHeaders(List.of("*")); // Mudar

        // Permite o envio de credenciais (como cookies) na requisição
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica essa configuração para todos os paths da sua API
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
