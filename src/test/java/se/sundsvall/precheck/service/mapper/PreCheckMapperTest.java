package se.sundsvall.precheck.service.mapper;

import static generated.client.partyassets.Status.ACTIVE;
import static generated.client.partyassets.Status.BLOCKED;
import static generated.client.partyassets.Status.EXPIRED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import generated.client.partyassets.Asset;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.precheck.api.model.Permit;

class PreCheckMapperTest {
	private static final List<Asset> ASSETS = List.of(
		new Asset().type("TYPE_1").status(ACTIVE),
		new Asset().type("TYPE_2").status(EXPIRED),
		new Asset().type("TYPE_3").status(BLOCKED).statusReason("statusReason"));

	@Test
	void toPermits() {
		final var assetType = "TYPE_1";

		final var result = PreCheckMapper.toPermits(ASSETS, assetType);

		assertThat(result).hasSize(1)
			.extracting(
				Permit::getReason,
				Permit::getType,
				Permit::isOrderable)
			.containsExactlyInAnyOrder(
				tuple(null, assetType, false));
	}

	@Test
	void toPermitsFilteredOnAssetType() {

		final var result = PreCheckMapper.toPermits(ASSETS, null);

		assertThat(result).hasSize(3)
			.extracting(
				Permit::getReason,
				Permit::getType,
				Permit::isOrderable)
			.containsExactlyInAnyOrder(
				tuple(null, "TYPE_1", false),
				tuple(null, "TYPE_2", true),
				tuple("statusReason", "TYPE_3", false));
	}
}
