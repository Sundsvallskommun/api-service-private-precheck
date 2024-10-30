package se.sundsvall.precheck.integration.partyassets;

import static se.sundsvall.precheck.integration.partyassets.configuration.PartyAssetsConfiguration.CLIENT_ID;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import generated.client.partyassets.Asset;
import generated.client.partyassets.Status;
import se.sundsvall.precheck.integration.partyassets.configuration.PartyAssetsConfiguration;

@FeignClient(value = CLIENT_ID, url = "${integration.party-assets.url}", configuration = PartyAssetsConfiguration.class, dismiss404 = true)
public interface PartyAssetsClient {

	/**
	 * Method for retrieving a list of assets.
	 *
	 * @param  personId                             the person ID, and the status of the asset, witch in this case should
	 *                                              always be "ACTIVE"
	 * @return                                      An ResponseEntity with a list of assets.
	 * @throws org.zalando.problem.ThrowableProblem when called service responds with error code.
	 */
	@GetMapping(path = "/assets", produces = "application/json")
	List<Asset> getPartyAssets(@RequestParam(name = "partyId") String partyId, @RequestParam(name = "status") Status status);
}
