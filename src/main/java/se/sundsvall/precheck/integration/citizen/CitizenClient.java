package se.sundsvall.precheck.integration.citizen;

import generated.client.citizen.CitizenExtended;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@FeignClient(name = CitizenIntegration.INTEGRATION_NAME, url = "${integration.citizen.base-url}", configuration = CitizenConfiguration.class)
public interface CitizenClient {

    /**
     * Method for retrieving a citizen.
     *
     * @param personId the person ID
     * @return An object with citizen data.
     * @throws org.zalando.problem.ThrowableProblem when called service responds with error code.
     */
    @GetMapping(path = "/{personId}", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<CitizenExtended> getCitizen(@PathVariable("personId") String personId);
}
