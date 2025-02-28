package se.sundsvall.precheck.integration.partyassets;

import static se.sundsvall.precheck.integration.partyassets.configuration.PartyAssetsConfiguration.CLIENT_ID;

import generated.client.partyassets.Asset;
import generated.client.partyassets.Status;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import se.sundsvall.precheck.integration.partyassets.configuration.PartyAssetsConfiguration;

@FeignClient(value = CLIENT_ID, url = "${integration.party-assets.url}", configuration = PartyAssetsConfiguration.class, dismiss404 = true)
public interface PartyAssetsClient {

	/**
	 * Method for retrieving a list of assets.
	 *
	 * @param  municipalityId                       the municipality ID
	 * @param  partyId                              the partyId,
	 * @param  status                               status of the asset, witch in this case should always be "ACTIVE"
	 * @return                                      An object with a list of assets.
	 * @throws org.zalando.problem.ThrowableProblem when called service responds with error code.
	 */
	@GetMapping(path = "/{municipalityId}/assets", produces = "application/json")
	List<Asset> getPartyAssets(
		@PathVariable(name = "municipalityId") String municipalityId,
		@RequestParam(name = "partyId") String partyId,
		@RequestParam(name = "status") Status status);
}
