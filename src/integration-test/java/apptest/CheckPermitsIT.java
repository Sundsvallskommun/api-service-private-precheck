package apptest;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.precheck.Application;

@WireMockAppTestSuite(
        files = "classpath:/PrivatePreCheck/",
        classes = Application.class
)
class CheckPermitsIT extends AbstractAppTest {

    private final String VALID_PRE_CHECK_ID = "d1d87cda-0dc5-41bb-9dd9-fa6ed2fd52ee";
    private final String VALID_MUNICIPALITY_ID = "2281";
    private final String TEMPLATE_SERVICE_PATH = "/PreCheck/%s?municipalityId=%s&assetType=%s";

    @BeforeEach
    void setUp() {
        CommonStubs.stubForAccessToken();
    }

    @AfterEach
    void tearDown() {
        verifyAllStubs();
        WireMock.resetAllRequests();
    }

    @Test
    void test1_ListResponse() {
        setupCall()
                .withServicePath(String.format(TEMPLATE_SERVICE_PATH, VALID_PRE_CHECK_ID, VALID_MUNICIPALITY_ID, ""))
                .withHttpMethod(HttpMethod.GET)
                .withExpectedResponseStatus(HttpStatus.OK)
                .withExpectedResponse("response.json")
                .sendRequestAndVerifyResponse();

    }

    @Test
    void test2_AssetTypeResponse() {
        setupCall()
                .withServicePath(String.format(TEMPLATE_SERVICE_PATH, VALID_PRE_CHECK_ID, VALID_MUNICIPALITY_ID, "PERMIT_NAME"))
                .withHttpMethod(HttpMethod.GET)
                .withExpectedResponseStatus(HttpStatus.OK)
                .withExpectedResponse("response.json")
                .sendRequestAndVerifyResponse();

    }

    @Test
    void test3_RequestAlreadyAcquiredPermit() {
        setupCall()
                .withServicePath(String.format(TEMPLATE_SERVICE_PATH, VALID_PRE_CHECK_ID, VALID_MUNICIPALITY_ID, "PERMIT"))
                .withHttpMethod(HttpMethod.GET)
                .withExpectedResponseStatus(HttpStatus.OK)
                .withExpectedResponse("response.json")
                .sendRequestAndVerifyResponse();

    }

    @Test
    void test4_QueryBadMunicipalityId() {
        setupCall()
                .withServicePath(String.format(TEMPLATE_SERVICE_PATH, VALID_PRE_CHECK_ID, "12456", "PERMIT_NAME"))
                .withHttpMethod(HttpMethod.GET)
                .withExpectedResponseStatus(HttpStatus.BAD_REQUEST)
                .withExpectedResponse("response.json")
                .sendRequestAndVerifyResponse();

    }

    @Test
    void test5_QueryBadCitizenAddressType() {
        setupCall()
                .withServicePath(String.format(TEMPLATE_SERVICE_PATH, VALID_PRE_CHECK_ID, VALID_MUNICIPALITY_ID, ""))
                .withHttpMethod(HttpMethod.GET)
                .withExpectedResponseStatus(HttpStatus.BAD_REQUEST)
                .withExpectedResponse("response.json")
                .sendRequestAndVerifyResponse();

    }

}