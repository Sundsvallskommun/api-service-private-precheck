package se.sundsvall.precheck.integration.citizen;

import generated.client.citizen.CitizenExtended;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CitizenIntegration {

    static final String INTEGRATION_NAME = "citizen";

    private static final Logger LOGGER = LoggerFactory.getLogger(CitizenIntegration.class);

    private final CitizenClient client;

    public CitizenIntegration(final CitizenClient client) {
        this.client = client;
    }

    public ResponseEntity<CitizenExtended> getCitizen(final String a) {
        try {
            LOGGER.info("Calling citizen service");
            return client.getCitizen(a);
        } catch (Exception e) {
            LOGGER.error("Error when calling citizen service: ", e);
            return ResponseEntity.notFound().build();
        }
    }

}
