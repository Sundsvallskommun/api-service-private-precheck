package se.sundsvall.precheck.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

@SuppressWarnings("unused")
@Service
public class OAuth2TokenService {
    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;


    public String getAccessToken() {
        OAuth2AuthenticationToken oauthToken =
                (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        String registrationId = oauthToken.getAuthorizedClientRegistrationId();

        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);


        OAuth2AuthorizedClient authorizedClient =
                new OAuth2AuthorizedClient(clientRegistration,
                        oauthToken.getName(),
                        oauthToken.getAccessToken());
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        return accessToken.getTokenValue();
    }
}