package se.sundsvall.precheck.integration.partyAssets;

import generated.client.partyAssets.Asset;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import se.sundsvall.precheck.integration.partyAssets.configuration.PartyAssetsConfiguration;
import se.sundsvall.precheck.service.utils.Util;

import static se.sundsvall.precheck.service.utils.Util;

import java.util.List;

@FeignClient(
        value = PartyAssetsConfiguration.CLIENT_ID,
        url = "${integration.party.base-url}",
        configuration = PartyAssetsConfiguration.class,
        dismiss404 = true
)
public interface PartyAssetsClient {

    @GetMapping(path = "/partyassets/1.0/assets", produces = "application/json")
    ResponseEntity<List<Asset>> getPartyAssets(@RequestParam(name = "partyId") String partyId,
                                               @RequestParam(name = "status") String status);

    @ExceptionHandler
    default ResponseEntity handleException(Exception e){
        return ResponseEntity.status(500).body(e.getMessage());
    };
}
