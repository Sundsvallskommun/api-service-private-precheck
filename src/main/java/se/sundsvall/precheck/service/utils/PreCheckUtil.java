package se.sundsvall.precheck.service.utils;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.ObjectUtils.anyNull;

import generated.client.citizen.CitizenExtended;
import java.util.Objects;

public final class PreCheckUtil {
	private static final String POPULATION_REGISTRATION_ADDRESS = "POPULATION_REGISTRATION_ADDRESS";

	private PreCheckUtil() {}

	public static boolean isCitizenOfMunicipality(CitizenExtended citizen, String municipalityId) {
		if (anyNull(citizen, municipalityId)) {
			return false;
		}

		return ofNullable(citizen.getAddresses()).orElse(emptyList()).stream()
			.filter(address -> POPULATION_REGISTRATION_ADDRESS.equalsIgnoreCase(address.getAddressType()))
			.anyMatch(address -> Objects.equals(address.getMunicipality(), municipalityId));
	}
}
