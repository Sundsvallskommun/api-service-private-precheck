package se.sundsvall.precheck.service.utils;

import static org.assertj.core.api.Assertions.assertThat;

import generated.client.citizen.CitizenAddress;
import generated.client.citizen.CitizenExtended;
import org.junit.jupiter.api.Test;

class PreCheckUtilTest {
	private static final String MUNICIPALITY_ID = "1234";
	private static final String POPULATION_REGISTRATION_ADDRESS = "POPULATION_REGISTRATION_ADDRESS";

	@Test
	void isCitizenOfMunicipality() {
		final var citizen = new CitizenExtended()
			.addAddressesItem(new CitizenAddress().municipality(MUNICIPALITY_ID).addressType(POPULATION_REGISTRATION_ADDRESS));

		assertThat(PreCheckUtil.isCitizenOfMunicipality(citizen, MUNICIPALITY_ID)).isTrue();
	}

	@Test
	void isCitizenOfMunicipalityWhenCitizenIsNull() {
		assertThat(PreCheckUtil.isCitizenOfMunicipality(null, MUNICIPALITY_ID)).isFalse();
	}

	@Test
	void isCitizenOfMunicipalityWhenMunicipalityIdIsNull() {
		assertThat(PreCheckUtil.isCitizenOfMunicipality(new CitizenExtended(), null)).isFalse();
	}

	@Test
	void isCitizenOfMunicipalityWhenCitizenAddressesIsNull() {
		final var citizen = new CitizenExtended();

		assertThat(PreCheckUtil.isCitizenOfMunicipality(citizen, MUNICIPALITY_ID)).isFalse();
	}

	@Test
	void isCitizenOfMunicipalityWhenValidAddressTypeIsMissing() {
		final var citizen = new CitizenExtended()
			.addAddressesItem(new CitizenAddress().municipality(MUNICIPALITY_ID).addressType("OTHER_ADDRESS_TYPE"));

		assertThat(PreCheckUtil.isCitizenOfMunicipality(citizen, MUNICIPALITY_ID)).isFalse();
	}

	@Test
	void isCitizenOfMunicipalityWhenAddressTypeContainsWrongMunicipalityId() {
		final var citizen = new CitizenExtended()
			.addAddressesItem(new CitizenAddress().municipality("2345").addressType(POPULATION_REGISTRATION_ADDRESS));

		assertThat(PreCheckUtil.isCitizenOfMunicipality(citizen, MUNICIPALITY_ID)).isFalse();
	}
}
