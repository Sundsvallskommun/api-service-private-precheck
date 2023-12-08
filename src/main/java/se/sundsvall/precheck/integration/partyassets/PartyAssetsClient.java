package se.sundsvall.precheck.integration.partyassets;

import generated.client.partyAssets.Asset;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static se.sundsvall.precheck.integration.partyassets.PartyAssetsIntegration.INTEGRATION_NAME;

@FeignClient(
        value = INTEGRATION_NAME,
        url = "${integration.party-assets.base-url}",
        configuration = PartyAssetsConfiguration.class,
        dismiss404 = true
)
public interface PartyAssetsClient {

    @GetMapping(path = "/assets", produces = "application/json")
    ResponseEntity<List<Asset>> getPartyAssets(@RequestParam(name = "partyId") String partyId,
                                               @RequestParam(name = "status") String status);
}


