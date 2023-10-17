package se.sundsvall.precheck.integration.partyAssets.configuration;

import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import se.sundsvall.dept44.configuration.feign.FeignConfiguration;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;
import se.sundsvall.dept44.configuration.feign.decoder.ProblemErrorDecoder;

@Import(FeignConfiguration.class)
public class PartyAssetsConfiguration {
    public static final String CLIENT_ID = "PartyAssets";


    FeignBuilderCustomizer feignBuilderCustomizer(ClientRegistrationRepository clientRepository, PartyAssetsProperties partyAssetsProperties) {
        return FeignMultiCustomizer.create()
                .withErrorDecoder(new ProblemErrorDecoder(CLIENT_ID))
                .withRequestTimeoutsInSeconds(partyAssetsProperties.connectTimeout(), partyAssetsProperties.readTimeout())
                .withRequestInterceptor(new BasicAuthRequestInterceptor("", ""))
                .composeCustomizersToOne();
    }

}
