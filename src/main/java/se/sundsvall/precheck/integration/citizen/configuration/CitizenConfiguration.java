package se.sundsvall.precheck.integration.Citizen.configuration;

import feign.Request;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import se.sundsvall.dept44.configuration.feign.FeignConfiguration;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;
import se.sundsvall.dept44.configuration.feign.decoder.ProblemErrorDecoder;

import java.time.Duration;
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
                .withErrorDecoder(errorDecoder())
                .withRequestOptions(feignOptions())
                .withRetryableOAuth2InterceptorForClientRegistration(clientRegistration())
                .composeCustomizersToOne();
    }


    @Value("${app.feign.config.token-url}")
    private String tokenUri;
    @Value("${citizen.oauth.client-id}")
    private String clientId;
    @Value("${citizen.oauth.client-secret}")
    private String clientSecret;

    private ClientRegistration clientRegistration() {
        return ClientRegistration.withRegistrationId(CLIENT_ID) //TODO REMOVE hard coded values
                .tokenUri(tokenUri)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .authorizationGrantType(new AuthorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS.getValue()))
                .build();
    }

    ErrorDecoder errorDecoder() {
        // We want to return 404 as a 404.
        return new ProblemErrorDecoder(CLIENT_ID, List.of(HttpStatus.NOT_FOUND.value()));
    }

    Request.Options feignOptions() {
        return new Request.Options(
                TimeUnit.SECONDS.convert(Duration.ofMillis(10000)), TimeUnit.SECONDS, // connect timeout in 10 seconds
                TimeUnit.SECONDS.convert(Duration.ofMillis(10000)), TimeUnit.SECONDS, // read timeout in 10 seconds
                false);
    }
}