package se.sundsvall.precheck.service;

import integrations.client.partyAssets.Status;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import se.sundsvall.precheck.api.model.PreCheckResponse;
import se.sundsvall.precheck.integration.citizen.CitizenClient;
import se.sundsvall.precheck.integration.partyAssets.PartyAssetsClient;

import java.util.List;
import java.util.Objects;

import static org.zalando.problem.Status.FORBIDDEN;
import static org.zalando.problem.Status.NO_CONTENT;
import static se.sundsvall.precheck.utils.PreCheckUtil.*;

@Service
public class PreCheckService {
    private static final Logger logger = LoggerFactory.getLogger(PreCheckService.class);
    private static final String INVALID_MUNICIPALITY_ID = "The owner of the party id '%s' is not registered in the municipality where the permit is requested";

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
        logger.debug("In data resourceNotFound: "+citizen + "    \n\n" + partyAssets);
         if (resourceNotFound(citizen, partyAssets)) { //TODO: fix always true
             logger.info("No citizen {} {} or party assets {} {} found for the given partyId: {}", citizen.getStatusCode(), citizen.getBody(), partyAssets.getStatusCode(),partyAssets.getBody(), partyId);
             throw Problem.valueOf(NO_CONTENT);
         }
        logger.debug("All didn't fail?");
        if (!hasValidMunicipalityId(citizen, municipalityId)) {
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
