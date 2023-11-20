package se.sundsvall.precheck.integration.partyassets.configuration;

import feign.Request;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import se.sundsvall.dept44.configuration.feign.FeignConfiguration;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;
import se.sundsvall.dept44.configuration.feign.decoder.ProblemErrorDecoder;

import java.util.concurrent.TimeUnit;

import static se.sundsvall.precheck.integration.partyassets.configuration.PartyAssetsIntegration.INTEGRATION_NAME;

@Import(FeignConfiguration.class)
public class PartyAssetsConfiguration {
    private final PartyAssetsProperties PARTY_ASSETS_PROPERTIES;

    public PartyAssetsConfiguration(PartyAssetsProperties partyAssetsProperties) {
        this.PARTY_ASSETS_PROPERTIES = partyAssetsProperties;
    }

    @Bean
    public FeignBuilderCustomizer feignBuilderCustomizer() {
        return FeignMultiCustomizer.create()
                .withErrorDecoder(new ProblemErrorDecoder(INTEGRATION_NAME))
                .withRequestOptions(feignOptions())
                .withRetryableOAuth2InterceptorForClientRegistration(clientRegistration())
                .composeCustomizersToOne();
    }

    private ClientRegistration clientRegistration() {
        return ClientRegistration.withRegistrationId(INTEGRATION_NAME)
                .tokenUri(PARTY_ASSETS_PROPERTIES.tokenUrl())
                .clientId(PARTY_ASSETS_PROPERTIES.oauthClientId())
                .clientSecret(PARTY_ASSETS_PROPERTIES.oauthClientSecret())
                .authorizationGrantType(new AuthorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS.getValue()))
                .build();
    }

    Request.Options feignOptions() {
        return new Request.Options(
                PARTY_ASSETS_PROPERTIES.connectTimeout().toMillis(), TimeUnit.MILLISECONDS,
                PARTY_ASSETS_PROPERTIES.readTimeout().toMillis(), TimeUnit.MILLISECONDS,
                false
        );
    }
}

