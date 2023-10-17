package se.sundsvall.precheck.integration.partyAssets;

import generated.client.partyAssets.Asset;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import se.sundsvall.precheck.integration.partyAssets.configuration.PartyAssetsConfiguration;
@FeignClient(
        value = PartyAssetsConfiguration.CLIENT_ID,
        url = "${app.feign.config.base-url}",
        configuration = PartyAssetsConfiguration.class,
        dismiss404 = true
)
public interface partyAssetsClient {

    @GetMapping(path = "/partyassets/1.0/assets", produces =  ("application/json"))
    Asset getPartyAssets(@PathVariable(name = "personId") final String personId);

}
