package com.codeQuest.Chatterly.Configurations;

import com.codeQuest.Chatterly.Entities.SimpleUserDetailsService;
import com.codeQuest.Chatterly.Security.JWT.JwtAuthenticationFilter;
import com.codeQuest.Chatterly.Security.JWT.JwtService;
import com.codeQuest.Chatterly.Security.Oauth2.CustomOAuth2UserService;
import com.codeQuest.Chatterly.Security.Oauth2.OAuth2LoginSuccessHandler;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final OAuth2LoginSuccessHandler oauthSuccessHandler;
    private final CustomOAuth2UserService oAuth2UserService;
    private final SimpleUserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/oauth2/**",
                                "/css/styles.css",
                                "/css/index.css",
                                "/images/profile.png",
                                "/",
                                "/home",
                                "/login",
                                "/signup"
                        ).permitAll()
                        .requestMatchers("/api/users/delete/{id}").hasRole("ADMIN")
                        .anyRequest(
                        ).authenticated()
                )
                .userDetailsService(userDetailsService)

                // Session management for stateless JWT
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                //  Authentication for JWT
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                //  Configuration for Oauth2 (Google and GitHub)
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .successHandler(oauthSuccessHandler)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService)
                        )
                )

                .formLogin(form -> form
                        .loginPage("/login").permitAll()
                        .successHandler((request, response, auth) -> {
                            UserDetails user = (UserDetails) auth.getPrincipal();
                            String token = jwtService.generateToken(user);

                            Cookie cookie = new Cookie("JWT", token);
                            cookie.setHttpOnly(true);
                            cookie.setSecure(true);
                            cookie.setPath("/");
                            response.addCookie(cookie);
                            response.sendRedirect("/home");
                        })
                )

                .securityContext(context -> context.requireExplicitSave(false))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"Unauthorized\"}");
                        })
                );

        return http.build();
    }

    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        final OidcUserService delegate = new OidcUserService();

        return (userRequest) -> {
            userRequest.getClientRegistration().getProviderDetails()
                    .getConfigurationMetadata()
                    .put("jwt.clock.skew", Duration.ofMinutes(5).toSeconds());

            return delegate.loadUser(userRequest);
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("*"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}