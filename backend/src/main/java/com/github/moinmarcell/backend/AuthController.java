package com.github.moinmarcell.backend;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/me")
    public DiscordUser me(@AuthenticationPrincipal OAuth2User principal) {
        if (principal instanceof DefaultOAuth2User defaultOAuth2User) {
            return new DiscordUser(
                    defaultOAuth2User.getAttribute("username"),
                    defaultOAuth2User.getAttribute("avatar")
            );
        }
        throw new UsernameNotFoundException("User not found");
    }

}
