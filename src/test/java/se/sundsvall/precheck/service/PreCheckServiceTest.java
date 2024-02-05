package se.sundsvall.precheck.service;

import static generated.client.partyassets.Status.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import generated.client.citizen.CitizenAddress;
import generated.client.citizen.CitizenExtended;
import generated.client.partyassets.Asset;
import se.sundsvall.precheck.integration.citizen.CitizenClient;
import se.sundsvall.precheck.integration.partyassets.PartyAssetsClient;

@ExtendWith(MockitoExtension.class)
class PreCheckServiceTest {

	@Mock
	private CitizenClient citizenClientMock;

	@Mock
	private PartyAssetsClient partyAssetClientMock;

	@InjectMocks
	private PreCheckService preCheckService;

	@Test
	void fetchPermits() {
		final var partyId = UUID.randomUUID().toString();
		final var municipalityId = "municipalityId";
		final var assetType = "assetType";

		when(citizenClientMock.getCitizen(partyId)).thenReturn(
			new CitizenExtended().addAddressesItem(
				new CitizenAddress()
					.addressType("POPULATION_REGISTRATION_ADDRESS")
					.municipality(municipalityId)));
		when(partyAssetClientMock.getPartyAssets(partyId, ACTIVE)).thenReturn(List.of(new Asset().type(assetType)));

		final var result = preCheckService.fetchPermits(partyId, municipalityId, assetType);

		assertThat(result).isNotNull();
		assertThat(result.isMunicipalCitizen()).isTrue();
		assertThat(result.getPermits()).hasSize(1);

		verify(citizenClientMock).getCitizen(partyId);
		verify(partyAssetClientMock).getPartyAssets(partyId, ACTIVE);
		verifyNoMoreInteractions(citizenClientMock, partyAssetClientMock);
	}

}
