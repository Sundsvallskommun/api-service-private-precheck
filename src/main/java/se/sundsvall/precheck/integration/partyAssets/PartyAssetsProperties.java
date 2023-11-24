package se.sundsvall.precheck.integration.partyAssets;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import se.sundsvall.precheck.integration.AbstractIntegrationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "integration.party-assets")
class PartyAssetsProperties extends AbstractIntegrationProperties {
    private OAuth2 oAuth2 = new OAuth2();
}
