package apptest;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.precheck.Application;

@WireMockAppTestSuite(files = "classpath:/PreCheckIT/", classes = Application.class)
class PreCheckIT extends AbstractAppTest {

	private static final String PARTY_ID = "2927622b-6540-431f-9841-4cb15fe8e2d6";
	private static final String SERVICE_PATH = "/precheck/%s?municipalityId=%s";

	@Test
	void test1_getPermitsWithoutFilters() {
		setupCall()
			.withServicePath(String.format(SERVICE_PATH, PARTY_ID, "2281"))
			.withHttpMethod(HttpMethod.GET)
			.withExpectedResponseStatus(HttpStatus.OK)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test2_getSpecificAssetType() {
		setupCall()
			.withServicePath(String.format(SERVICE_PATH + "&assetType=%s", PARTY_ID, "2281", "PARKING_PERMIT"))
			.withHttpMethod(HttpMethod.GET)
			.withExpectedResponseStatus(HttpStatus.OK)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test3_getNonMunicipalityCitizen() {
		setupCall()
			.withServicePath(String.format(SERVICE_PATH, PARTY_ID, "2282"))
			.withHttpMethod(HttpMethod.GET)
			.withExpectedResponseStatus(HttpStatus.OK)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse();
	}
}
