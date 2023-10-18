package se.sundsvall.precheck.integration.citizen.configuration;

import feign.Request;
import feign.codec.ErrorDecoder;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import se.sundsvall.dept44.configuration.feign.FeignConfiguration;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;
import se.sundsvall.dept44.configuration.feign.decoder.ProblemErrorDecoder;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Import(FeignConfiguration.class)
public class CitizenConfiguration {

    public static final String CLIENT_ID = "Citizen";

    private final CitizenProperties citizenProperties;
    public CitizenConfiguration(CitizenProperties citizenProperties) {
        this.citizenProperties = citizenProperties;
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
                .tokenUri(citizenProperties.getTokenUrl())
                .clientId(citizenProperties.getOauthClientId())
                .clientSecret(citizenProperties.getOauthClientSecret())
                .authorizationGrantType(new AuthorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS.getValue()))
                .build();
    }

    ErrorDecoder errorDecoder() {
        //Return 404 as a 404.
        return new ProblemErrorDecoder(CLIENT_ID, List.of(HttpStatus.NOT_FOUND.value()));
    }

    Request.Options feignOptions() {
        return new Request.Options(
                citizenProperties.getConnectTimeout().toMillis(), TimeUnit.MILLISECONDS,
                citizenProperties.getReadTimeout().toMillis(), TimeUnit.MILLISECONDS,
                false
        );
    }
}
