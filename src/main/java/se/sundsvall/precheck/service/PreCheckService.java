package se.sundsvall.precheck.service;

import generated.client.partyAssets.Status;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import se.sundsvall.precheck.api.model.PreCheckResponse;
import se.sundsvall.precheck.integration.citizen.CitizenClient;
import se.sundsvall.precheck.integration.partyAssets.PartyAssetsClient;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.zalando.problem.Status.FORBIDDEN;
import static org.zalando.problem.Status.NO_CONTENT;
import static se.sundsvall.precheck.service.utils.PreCheckUtil.*;

@Service
public class PreCheckService {
    private static final Logger logger = LoggerFactory.getLogger(PreCheckService.class);
    private static final String INVALID_MUNICIPALITY_ID = "The owner of the partyId '%s' is not registered in the municipality where the permit is requested";
    private static final String NO_CONTENT_FOUND = "No content found for the given partyId: %s";

    private final CitizenClient citizenClient;
    private final PartyAssetsClient partyAssetsClient;


    public PreCheckService(CitizenClient citizenClient, PartyAssetsClient partyAssetsClient) {
        this.citizenClient = Objects.requireNonNull(citizenClient);
        this.partyAssetsClient = Objects.requireNonNull(partyAssetsClient);
    }

    public ResponseEntity<List<PreCheckResponse>> checkPermit(String partyId, String municipalityId, String assetType) {
        partyId = StringUtils.trimToEmpty(partyId);
        municipalityId = StringUtils.trimToEmpty(municipalityId);

        var citizen = citizenClient.getCitizen(partyId);
        var partyAssets = partyAssetsClient.getPartyAssets(partyId, String.valueOf(Status.ACTIVE));
        logger.debug("In data resourceNotFound: " + citizen + "    \n\n" + partyAssets);

        if (resourceNotFound(citizen, partyAssets)) {
            logger.info(String.format("No citizen {} {} or party assets {} {} found for the given partyId: {}", citizen.getStatusCode(), Optional.ofNullable(citizen.getBody()), partyAssets.getStatusCode(), partyAssets.getBody(), partyId));
            throw Problem.valueOf(NO_CONTENT, String.format(NO_CONTENT_FOUND, partyId));
        }

        if (hasInValidMunicipalityId(citizen, municipalityId)) {
            logger.info("No valid Municipality ID found during the check for partyId: {}", partyId);
            throw Problem.valueOf(FORBIDDEN, String.format(INVALID_MUNICIPALITY_ID, partyId));
        }

        if (!StringUtils.isEmpty(assetType)) {
            logger.info("Didn't have any assetType");
            return handleAssetType(assetType, partyAssets);
        }

        return handleNoGivenAssetType(partyAssets);

    }
}
