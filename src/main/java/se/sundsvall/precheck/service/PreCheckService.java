package se.sundsvall.precheck.service;

import generated.client.partyAssets.Status;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import se.sundsvall.precheck.integration.citizen.CitizenClient;
import se.sundsvall.precheck.integration.partyAssets.PartyAssetsClient;

import java.util.Objects;

import static se.sundsvall.precheck.utils.PreCheckUtil.*;


@Service
public class PreCheckService {
    // Initialize logger for the PreCheckService class
    private static final Logger logger = LoggerFactory.getLogger(PreCheckService.class);

    // Initialize PreCheckUtil, CitizenClient, and PartyAssetsClient
    private final CitizenClient citizenClient;
    private final PartyAssetsClient partyAssetsClient;

    // Constructor for PreCheckService to initialize required components
    @Autowired
    public PreCheckService(CitizenClient citizenClient, PartyAssetsClient partyAssetsClient) {
        this.citizenClient = Objects.requireNonNull(citizenClient);
        this.partyAssetsClient = Objects.requireNonNull(partyAssetsClient);
    }

    // Method to check permits based on partyId, municipalityId, and assetType
    public ResponseEntity<?> checkPermit(String partyId, String municipalityId, String assetType) {
        // Trim and clean the inputs
        partyId = StringUtils.trimToEmpty(partyId);
        municipalityId = StringUtils.trimToEmpty(municipalityId);

        // Check if the provided partyId and municipalityId are in the correct format and if any of the parameters are empty
        if (!isValidIds(partyId, municipalityId)) {
            return handleInvalidIds(partyId, municipalityId, assetType);
        }

        // Retrieve citizen and party assets using the provided partyId
        var citizen = citizenClient.getCitizen(partyId);
        var party = partyAssetsClient.getPartyAssets(partyId, String.valueOf(Status.ACTIVE));

        //Check if the api calls was successful
        if (resourceNotFound(citizen, party)) {
            logger.info("No citizen {} {} or party assets {} {} found for the given partyId: {}", citizen.getStatusCode(), citizen.getBody(), party.getStatusCode(),party.getBody(), partyId);
            return buildPrecheckResponseEntity(HttpStatus.NOT_FOUND, assetType, false, "No citizen or party assets found for the given partyId");
        }

        // Check if any valid MunicipalityId is associated with the PartyId
        if (!hasValidMunicipalityId(citizen, municipalityId) ){
            logger.info("No valid Municipality ID found during the check for partyId: {}", partyId);
            return buildPrecheckResponseEntity(HttpStatus.NOT_FOUND, assetType, false, "No valid Municipality ID connected to the given partyId");
        }

        if (!StringUtils.isEmpty(assetType)) {
            return handleAssetType(assetType, String.valueOf(party));
        }

        return handleNoGivenAssetType(party, partyId);

    }
}
