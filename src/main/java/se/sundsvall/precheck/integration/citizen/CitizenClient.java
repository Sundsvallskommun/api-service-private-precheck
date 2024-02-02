package se.sundsvall.precheck.integration.citizen;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import generated.client.citizen.CitizenExtended;
import se.sundsvall.precheck.integration.citizen.configuration.CitizenConfiguration;

@FeignClient(name = CitizenConfiguration.CLIENT_ID, url = "${integration.citizen.url}", configuration = CitizenConfiguration.class)
public interface CitizenClient {

    /**
     * Method for retrieving a citizen.
     *
     * @param personId the person ID
     * @return An object with citizen data.
     * @throws org.zalando.problem.ThrowableProblem when called service responds with error code.
     */
    @GetMapping(path = "/{personId}", produces = APPLICATION_JSON_VALUE)
	CitizenExtended getCitizen(@PathVariable("personId") String personId);
}
