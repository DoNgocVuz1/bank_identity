package com.example.bank_identity.config;

import com.example.bank_identity.security.JwtAuthenticationEntryPoint;
import com.example.bank_identity.security.JwtAccessDeniedHandler;
import com.example.bank_identity.security.CustomUserDetailsService;
import com.example.bank_identity.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;
    private final CustomUserDetailsService userDetailsService;  // dùng chung cho cả 2 chain
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    //DÙNG CHUNG

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // DaoAuthenticationProvider: dùng chung – cả Session lẫn JWT đều xác thực qua đây
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // ← dùng chung CustomUserDetailsService
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // AuthenticationManager: dùng chung – AuthController gọi cái này để xác thực JWT login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // SESSION – cho Web browser (/web/**)
    // @Order(1) ưu tiên cao hơn, được check trước
    @Bean
    @Order(1)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/web/**")   // ← CHỈ áp dụng cho URL bắt đầu bằng /web/
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // tạo session khi cần
                        .maximumSessions(1)       // 1 tài khoản chỉ login 1 nơi cùng lúc
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/web/login").permitAll()// trang login không cần auth
                        .requestMatchers("/web/admin/**").hasRole("ADMIN")// chỉ ADMIN
                        .anyRequest().authenticated()// /web/dashboard cần login
                )
                .formLogin(form -> form
                        .loginPage("/web/login")// URL trang login tự làm
                        .loginProcessingUrl("/web/login")// Spring Security tự xử lý POST này
                        .defaultSuccessUrl("/web/dashboard", true)  // sau login thành công → đến đây
                        .failureUrl("/web/login?error=true")  // sai mật khẩu → quay lại login
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/web/logout")
                        .logoutSuccessUrl("/web/login?logout=true")
                        .invalidateHttpSession(true)   // xóa session
                        .deleteCookies("JSESSIONID")// xóa cookie session
                        .permitAll()
                )
                .authenticationProvider(authenticationProvider());// ← dùng chung provider

        return http.build();
    }

    // JWT – cho Mobile / API (/api/**)
    // @Order(2) check sau chain 1
    @Bean
    @Order(2)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")   // ← CHỈ áp dụng cho URL bắt đầu bằng /api/
                .csrf(csrf -> csrf.disable()) // API thì TẮT csrf
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // KHÔNG tạo session
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/test/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/users/**").authenticated()
                        .anyRequest().authenticated()
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authenticationProvider(authenticationProvider()) // ← dùng chung provider
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ── CHAIN 3: Static resources + Swagger
    @Bean
    @Order(3)
    public SecurityFilterChain staticFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(
                        "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html",
                        "/", "/static/**", "/css/**", "/js/**", "/login.html"
                )
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }
}