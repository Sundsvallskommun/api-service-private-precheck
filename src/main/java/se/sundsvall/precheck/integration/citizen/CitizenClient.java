package se.sundsvall.precheck.integration.citizen;

import generated.client.citizen.CitizenExtended;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import se.sundsvall.precheck.integration.citizen.configuration.CitizenConfiguration;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
@FeignClient(
        value = CitizenConfiguration.CLIENT_ID,
        url = "${integration.citizen.base-url}",
        configuration = CitizenConfiguration.class,
        dismiss404 = true
)
public interface CitizenClient {
    @GetMapping(path = "/citizen/2.0/{personId}", produces =  (APPLICATION_JSON_VALUE))
    ResponseEntity<CitizenExtended> getCitizen(@PathVariable(name = "personId") final String personId);
}


