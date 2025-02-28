package se.sundsvall.precheck.service;

import static generated.client.partyassets.Status.ACTIVE;
import static se.sundsvall.precheck.service.mapper.PreCheckMapper.toPermits;
import static se.sundsvall.precheck.service.utils.PreCheckUtil.isCitizenOfMunicipality;

import org.springframework.stereotype.Service;
import se.sundsvall.precheck.api.model.PreCheckResponse;
import se.sundsvall.precheck.integration.citizen.CitizenClient;
import se.sundsvall.precheck.integration.partyassets.PartyAssetsClient;

@Service
public final class PreCheckService {

	private final CitizenClient citizenClient;
	private final PartyAssetsClient partyAssetsClient;

	public PreCheckService(final CitizenClient citizenClient, final PartyAssetsClient partyAssetsClient) {
		this.citizenClient = citizenClient;
		this.partyAssetsClient = partyAssetsClient;
	}

	public PreCheckResponse fetchPermits(final String partyId, final String municipalityId, final String assetType) {
		final var citizen = citizenClient.getCitizen(municipalityId, partyId);
		final var partyAssets = partyAssetsClient.getPartyAssets(municipalityId, partyId, ACTIVE);

		return PreCheckResponse.builder()
			.withMunicipalCitizen(isCitizenOfMunicipality(citizen, municipalityId))
			.withPermits(toPermits(partyAssets, assetType))
			.build();
	}
}
