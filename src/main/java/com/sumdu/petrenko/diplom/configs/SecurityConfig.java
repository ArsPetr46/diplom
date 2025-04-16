package com.sumdu.petrenko.diplom.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Конфігурація безпеки для Spring додатку.
 * <p>
 * Вимикає CSRF захист, дозволяє доступ до Swagger UI та API документації без аутентифікації,
 * а також налаштовує базову HTTP аутентифікацію для всіх інших запитів.
 * </p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    /**
     * Налаштування фільтру безпеки.
     *
     * @param http об'єкт HttpSecurity для налаштування безпеки
     * @return побудований SecurityFilterChain
     * @throws Exception у разі помилки налаштування безпеки
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
