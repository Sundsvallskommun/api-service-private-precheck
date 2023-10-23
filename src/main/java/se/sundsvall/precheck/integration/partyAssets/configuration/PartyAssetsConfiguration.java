package se.sundsvall.precheck.integration.partyAssets.configuration;

import feign.Request;
import feign.codec.ErrorDecoder;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import se.sundsvall.dept44.configuration.feign.FeignConfiguration;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;
import se.sundsvall.dept44.configuration.feign.decoder.ProblemErrorDecoder;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Import(FeignConfiguration.class)
public class PartyAssetsConfiguration {
    public static final String CLIENT_ID = "PartyAssets";

    private final PartyAssetsProperties partyAssetsProperties;

    public PartyAssetsConfiguration(PartyAssetsProperties partyAssetsProperties) {
        this.partyAssetsProperties = partyAssetsProperties;
    }

    @Bean
    public FeignBuilderCustomizer feignBuilderCustomizer() {
        return FeignMultiCustomizer.create()
                .withErrorDecoder(new ProblemErrorDecoder(CLIENT_ID))
                .withRequestOptions(feignOptions())
                .withRetryableOAuth2InterceptorForClientRegistration(clientRegistration())
                .composeCustomizersToOne();
    }

    private ClientRegistration clientRegistration() {
        return ClientRegistration.withRegistrationId(CLIENT_ID)
                .tokenUri(partyAssetsProperties.tokenUrl())
                .clientId(partyAssetsProperties.oauthClientId())
                .clientSecret(partyAssetsProperties.oauthClientSecret())
                .authorizationGrantType(new AuthorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS.getValue()))
                .build();
    }

    Request.Options feignOptions() {
        return new Request.Options(
                partyAssetsProperties.connectTimeout().toMillis(), TimeUnit.MILLISECONDS,
                partyAssetsProperties.readTimeout().toMillis(), TimeUnit.MILLISECONDS,
                false
        );
    }
}

