package se.sundsvall.precheck.integration.citizen.configuration;

import feign.Request;
import feign.codec.ErrorDecoder;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
@Getter
@AllArgsConstructor
public class CitizenConfiguration {

    public static final String CLIENT_ID = "Citizen";

    private final CitizenProperties citizenProperties;

    @Bean
    public FeignBuilderCustomizer feignBuilderCustomizer() {
        return FeignMultiCustomizer.create()
                .withErrorDecoder(errorDecoder())
                .withRequestOptions(createFeignOptions())
                .withRetryableOAuth2InterceptorForClientRegistration(clientRegistration())
                .composeCustomizersToOne();
    }

    private ClientRegistration clientRegistration() {
        return ClientRegistration.withRegistrationId(CLIENT_ID)
                .tokenUri(citizenProperties.tokenUrl())
                .clientId(citizenProperties.oauthClientId())
                .clientSecret(citizenProperties.oauthClientSecret())
                .authorizationGrantType(new AuthorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS.getValue()))
                .build();
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new ProblemErrorDecoder(CLIENT_ID, List.of(HttpStatus.NOT_FOUND.value()));
    }

    private Request.Options createFeignOptions() {
        return new Request.Options(
                citizenProperties.connectTimeout().toMillis(), TimeUnit.MILLISECONDS,
                citizenProperties.readTimeout().toMillis(), TimeUnit.MILLISECONDS,
                false
        );
    }
}