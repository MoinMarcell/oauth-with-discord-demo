package com.github.moinmarcell.backend;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.moinmarcell.backend.SecurityConfig.DISCORD_BOT_USER_AGENT;

public class RestOAuth2AccessTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {
    private final RestOperations restOperations;

    public RestOAuth2AccessTokenResponseClient(RestOperations restOperations) {
        this.restOperations = restOperations;
    }

    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) throws OAuth2AuthenticationException {
        ClientRegistration clientRegistration = authorizationGrantRequest.getClientRegistration();

        String tokenUri = clientRegistration.getProviderDetails().getTokenUri();

        MultiValueMap<String, String> tokenRequest = new LinkedMultiValueMap<>();
        tokenRequest.add("client_id", clientRegistration.getClientId());
        tokenRequest.add("client_secret", clientRegistration.getClientSecret());
        tokenRequest.add("grant_type", clientRegistration.getAuthorizationGrantType().getValue());
        tokenRequest.add("code", authorizationGrantRequest.getAuthorizationExchange().getAuthorizationResponse().getCode());
        tokenRequest.add("redirect_uri", authorizationGrantRequest.getAuthorizationExchange().getAuthorizationRequest().getRedirectUri());
        tokenRequest.add("scope", String.join(" ", authorizationGrantRequest.getClientRegistration().getScopes()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add(HttpHeaders.USER_AGENT, DISCORD_BOT_USER_AGENT);

        ResponseEntity<AccessResponse> response =
                restOperations.exchange(tokenUri, HttpMethod.POST, new HttpEntity<>(tokenRequest, headers), AccessResponse.class);

        AccessResponse accessResponse = response.getBody();

        assert accessResponse != null;
        Set<String> scopes = accessResponse.getScopes().isEmpty() ?
                authorizationGrantRequest.getAuthorizationExchange().getAuthorizationRequest().getScopes() : accessResponse.getScopes();

        return OAuth2AccessTokenResponse.withToken(accessResponse.getAccessToken())
                .tokenType(accessResponse.getTokenType())
                .expiresIn(accessResponse.getExpiresIn())
                .scopes(scopes)
                .build();
    }

    static class AccessResponse {
        @Getter
        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("token_type")
        private String tokenType;

        @Getter
        @JsonProperty("expires_in")
        private int expiresIn;

        private String scope;

        public AccessResponse() {
        }

        AccessResponse(String accessToken, String tokenType, int expiresIn, String scope) {
            this.accessToken = accessToken;
            this.tokenType = tokenType;
            this.expiresIn = expiresIn;
            this.scope = scope;
        }

        public OAuth2AccessToken.TokenType getTokenType() {
            return OAuth2AccessToken.TokenType.BEARER.getValue().equalsIgnoreCase(tokenType) ? OAuth2AccessToken.TokenType.BEARER : null;
        }

        public Set<String> getScopes() {
            return StringUtils.hasText(scope) ? Stream.of(scope.split(" ")).collect(Collectors.toSet()) : Collections.emptySet();
        }
    }
}