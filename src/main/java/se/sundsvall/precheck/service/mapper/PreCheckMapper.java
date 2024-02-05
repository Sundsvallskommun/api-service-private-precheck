package se.sundsvall.precheck.service.mapper;

import static generated.client.partyassets.Status.EXPIRED;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.Objects;

import generated.client.partyassets.Asset;
import se.sundsvall.precheck.api.model.Permit;

public class PreCheckMapper {
	private PreCheckMapper() {}

	public static List<Permit> toPermits(List<Asset> partyAssets, String assetType) {
		return ofNullable(partyAssets).orElse(emptyList()).stream()
			.filter(partyAsset -> isNull(assetType) || assetType.equals(partyAsset.getType()))
			.map(PreCheckMapper::toPermit)
			.toList();
	}

	private static Permit toPermit(Asset partyAsset) {
		return Permit.builder()
			.withOrderable(Objects.equals(EXPIRED, partyAsset.getStatus()))
			.withReason(partyAsset.getStatusReason())
			.withType(partyAsset.getType())
			.build();
	}

}
