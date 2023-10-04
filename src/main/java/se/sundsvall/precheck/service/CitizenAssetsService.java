package se.sundsvall.precheck.service;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;
import se.sundsvall.precheck.api.model.TestData;
import se.sundsvall.precheck.integration.citizen.CitizenClient;


@Service
public class CitizenAssetsService {
    @NotNull
    @Contract(pure = true)
    public static TestData checkPermit(String partyId, String assetType, String municipalityId) {
        CitizenClient citizenClient = Feign.builder()
                .decoder(new JacksonDecoder())
                .target(CitizenClient.class, "http://localhost:3000");
        System.out.println("citizenClient: " + citizenClient);

        TestData testData = citizenClient.getTestDataJson();
        return testData;

    }
}

/*
 public String getAccessToken() {
        OAuth2AuthenticationToken oauthToken =
            (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        String registrationId = oauthToken.getAuthorizedClientRegistrationId();

        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);
        OAuth2AuthorizedClient authorizedClient =
            new OAuth2AuthorizedClient(clientRegistration, oauthToken.getName(), oauthToken.getAccessToken());

        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        return accessToken.getTokenValue();
    }
 */