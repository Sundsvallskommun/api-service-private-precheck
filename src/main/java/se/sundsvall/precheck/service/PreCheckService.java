package se.sundsvall.precheck.service;

import integrations.client.partyAssets.Status;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import se.sundsvall.precheck.integration.citizen.CitizenClient;
import se.sundsvall.precheck.integration.partyAssets.PartyAssetsClient;

import java.util.Objects;

import static se.sundsvall.precheck.utils.PreCheckUtil.*;


@Service
public class PreCheckService {
    private static final Logger logger = LoggerFactory.getLogger(PreCheckService.class);

    private final CitizenClient citizenClient;
    private final PartyAssetsClient partyAssetsClient;


    public PreCheckService(CitizenClient citizenClient, PartyAssetsClient partyAssetsClient) {
        this.citizenClient = Objects.requireNonNull(citizenClient);
        this.partyAssetsClient = Objects.requireNonNull(partyAssetsClient);
    }

    public ResponseEntity<?> checkPermit(String partyId, String municipalityId, String assetType) {
        partyId = StringUtils.trimToEmpty(partyId);
        municipalityId = StringUtils.trimToEmpty(municipalityId);

        var citizen = citizenClient.getCitizen(partyId);
        var partyAssets = partyAssetsClient.getPartyAssets(partyId, String.valueOf(Status.ACTIVE));

        if (resourceNotFound(citizen, partyAssets)) {
            logger.info("No citizen {} {} or party assets {} {} found for the given partyId: {}", citizen.getStatusCode(), citizen.getBody(), partyAssets.getStatusCode(),partyAssets.getBody(), partyId);
            return buildPrecheckResponseEntity(HttpStatus.NOT_FOUND, assetType, false, "No citizen or party assets found for the given partyId");
        }

        if (!hasValidMunicipalityId(citizen, municipalityId) ){
            logger.info("No valid Municipality ID found during the check for partyId: {}", partyId);
            return buildPrecheckResponseEntity(HttpStatus.NOT_FOUND, assetType, false, "No valid Municipality ID connected to the given partyId");
        }

        if (!StringUtils.isEmpty(assetType)) {
            return handleAssetType(assetType, partyAssets);
        }

        return handleNoGivenAssetType(partyAssets, partyId);

    }
}
