package com.github.moinmarcell.backend;

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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(l -> l.logoutSuccessUrl("/").permitAll())
                .authorizeHttpRequests(a -> a
                        .anyRequest().authenticated()
                )
                .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .oauth2Login(c -> {
                    try {
                        c.init(http);
                        c.tokenEndpoint(t -> t.accessTokenResponseClient(new RestOAuth2AccessTokenResponseClient(restOperations())));
                        c.userInfoEndpoint(u -> u.userService(new RestOAuth2UserService(restOperations())));
                        c.defaultSuccessUrl("/", true);
                    } catch (Exception e) {
                        throw new IllegalArgumentException(e);
                    }
                });
        return http.build();
    }

    @Bean
    public RestOperations restOperations() {
        return new RestTemplate();
    }

    public static final String DISCORD_BOT_USER_AGENT = "DiscordBot (https://github.com/fourscouts/blog/tree/master/oauth2-discord)";
}
