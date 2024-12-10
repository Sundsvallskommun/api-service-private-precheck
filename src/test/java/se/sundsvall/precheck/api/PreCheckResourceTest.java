package se.sundsvall.precheck.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.precheck.Application;
import se.sundsvall.precheck.api.model.Permit;
import se.sundsvall.precheck.api.model.PreCheckResponse;
import se.sundsvall.precheck.service.PreCheckService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class PreCheckResourceTest {

	private static final String PARTY_ID = "7a1a2472-f100-437d-8702-f7dfb5e79efe";
	private static final String MUNICIPALITY_ID = "2281";
	private static final String ASSET_TYPE = "PARKING_PERMIT";

	@MockitoBean
	private PreCheckService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void precheckWithAssetTypeFilter() throws Exception {

		final var serviceResult = PreCheckResponse.builder()
			.withMunicipalCitizen(false)
			.withPermits(List.of(Permit.builder()
				.withOrderable(false)
				.withReason("reason")
				.withType(ASSET_TYPE)
				.build()))
			.build();

		when(serviceMock.fetchPermits(any(), any(), eq(ASSET_TYPE))).thenReturn(serviceResult);

		final var response = webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/precheck/" + PARTY_ID)
				.queryParam("municipalityId", MUNICIPALITY_ID)
				.queryParam("assetType", ASSET_TYPE)
				.build())
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(PreCheckResponse.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isEqualTo(serviceResult);
		verify(serviceMock).fetchPermits(PARTY_ID, MUNICIPALITY_ID, ASSET_TYPE);
	}

	@Test
	void precheckWithoutFilters() throws Exception {
		final var serviceResult = PreCheckResponse.builder()
			.withMunicipalCitizen(true)
			.withPermits(List.of(
				Permit.builder()
					.withOrderable(false)
					.withReason("reason")
					.withType(ASSET_TYPE)
					.build(),
				Permit.builder()
					.withOrderable(true)
					.withType("ALKT_PERMIT")
					.build()))
			.build();

		when(serviceMock.fetchPermits(any(), any(), isNull())).thenReturn(serviceResult);

		final var response = webTestClient.get()
			.uri(uriBuilder -> uriBuilder.path("/precheck/" + PARTY_ID)
				.queryParam("municipalityId", MUNICIPALITY_ID)
				.build())
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(PreCheckResponse.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isEqualTo(serviceResult);
		verify(serviceMock).fetchPermits(PARTY_ID, MUNICIPALITY_ID, null);
	}
}
