package com.github.moinmarcell.backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${com.github.moinmarcell.url}")
    private String url;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(a -> a
                        .anyRequest().permitAll()
                )
                .logout(l -> l.logoutSuccessUrl(url).permitAll())
                .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .oauth2Login(c -> {
                    c.tokenEndpoint(t -> t.accessTokenResponseClient(new RestOAuth2AccessTokenResponseClient(restOperations())));
                    c.userInfoEndpoint(u -> u.userService(new RestOAuth2UserService(restOperations())));
                    c.defaultSuccessUrl(url, true);
                });
        return http.build();
    }

    @Bean
    public RestOperations restOperations() {
        return new RestTemplate();
    }

    public static final String DISCORD_BOT_USER_AGENT = "DiscordBot (https://github.com/MoinMarcell/oauth-with-discord-demo)";
}
