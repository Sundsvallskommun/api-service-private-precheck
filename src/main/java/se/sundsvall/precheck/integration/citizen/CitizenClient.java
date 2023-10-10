package se.sundsvall.precheck.integration.Citizen;

import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import se.sundsvall.precheck.api.model.PrecheckResponse;
import se.sundsvall.precheck.integration.Citizen.configuration.CitizenConfiguration;

import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@FeignClient(
        value = CitizenConfiguration.CLIENT_ID,
        url = "${app.feign.config.base-url}/citizen/2.0",
        configuration = CitizenConfiguration.class,
        dismiss404 = true
)
public interface CitizenClient {
    @GetMapping(path = "/{personId}", produces =  (APPLICATION_JSON_VALUE))
    Optional<String> getCitizen(@PathVariable(name = "personId") final String personId);
}


