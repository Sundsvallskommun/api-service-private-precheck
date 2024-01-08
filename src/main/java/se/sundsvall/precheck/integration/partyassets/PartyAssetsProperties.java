package se.sundsvall.precheck.integration.partyassets;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import se.sundsvall.precheck.integration.AbstractIntegrationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "integration.party-assets")
class PartyAssetsProperties extends AbstractIntegrationProperties {
    private final OAuth2 oAuth2 = new OAuth2();
}
