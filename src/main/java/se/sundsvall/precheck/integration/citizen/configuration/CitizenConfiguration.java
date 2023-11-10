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
    private final CitizenProperties citizenProperties;

    public CitizenConfiguration(CitizenProperties citizenProperties) {
        this.citizenProperties = citizenProperties;
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
                .tokenUri(citizenProperties.tokenUrl())
                .clientId(citizenProperties.oauthClientId())
                .clientSecret(citizenProperties.oauthClientSecret())
                .authorizationGrantType(new AuthorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS.getValue()))
                .build();
    }

    Request.Options createFeignOptions() {
        return new Request.Options(
                citizenProperties.connectTimeout().toMillis(), TimeUnit.MILLISECONDS,
                citizenProperties.readTimeout().toMillis(), TimeUnit.MILLISECONDS,
                false
        );
    }
}