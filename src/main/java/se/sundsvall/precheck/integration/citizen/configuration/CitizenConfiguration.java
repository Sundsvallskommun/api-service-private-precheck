package se.sundsvall.precheck.integration.citizen.configuration;

import feign.Request;
import lombok.Getter;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import se.sundsvall.dept44.configuration.feign.FeignConfiguration;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;
import se.sundsvall.dept44.configuration.feign.decoder.ProblemErrorDecoder;

import java.util.concurrent.TimeUnit;

@Getter
@Import(FeignConfiguration.class)
public class CitizenConfiguration {
    private final CitizenProperties CITIZEN_PROPERTIES;

    public CitizenConfiguration(CitizenProperties CITIZEN_PROPERTIES) {
        this.CITIZEN_PROPERTIES = CITIZEN_PROPERTIES;
    }

    @Bean
    public FeignBuilderCustomizer feignBuilderCustomizer() {
        return FeignMultiCustomizer.create()
                .withErrorDecoder(new ProblemErrorDecoder(CitizenIntegration.INTEGRATION_NAME))
                .withRequestOptions(createFeignOptions())
                .withRetryableOAuth2InterceptorForClientRegistration(clientRegistration())
                .composeCustomizersToOne();
    }

    public ClientRegistration clientRegistration() {
        return ClientRegistration.withRegistrationId(CitizenIntegration.INTEGRATION_NAME)
                .tokenUri(CITIZEN_PROPERTIES.tokenUrl())
                .clientId(CITIZEN_PROPERTIES.oauthClientId())
                .clientSecret(CITIZEN_PROPERTIES.oauthClientSecret())
                .authorizationGrantType(new AuthorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS.getValue()))
                .build();
    }

    Request.Options createFeignOptions() {
        return new Request.Options(
                CITIZEN_PROPERTIES.connectTimeout().toMillis(), TimeUnit.MILLISECONDS,
                CITIZEN_PROPERTIES.readTimeout().toMillis(), TimeUnit.MILLISECONDS,
                false
        );
    }
}