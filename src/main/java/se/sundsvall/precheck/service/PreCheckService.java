package se.sundsvall.precheck.service;


import generated.client.partyAssets.Status;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import se.sundsvall.precheck.api.model.PermitListResponse;
import se.sundsvall.precheck.integration.citizen.CitizenClient;
import se.sundsvall.precheck.integration.partyAssets.PartyAssetsClient;
import se.sundsvall.precheck.utils.PreCheckUtil;

import java.util.Objects;

import static se.sundsvall.precheck.utils.Constants.MUNICIPALITY_ID_REGEX;
import static se.sundsvall.precheck.utils.Constants.PARTY_ID_REGEX;

@Service
public class PreCheckService {
    private static final Logger logger = LoggerFactory.getLogger(PreCheckService.class);
    private final PreCheckUtil util = new PreCheckUtil();
    private final CitizenClient citizenClient;
    private final PartyAssetsClient partyAssetsClient;

    @Autowired
    public PreCheckService(CitizenClient citizenClient, PartyAssetsClient partyAssetsClient) {
        this.citizenClient = Objects.requireNonNull(citizenClient, "CitizenClient must not be null");
        this.partyAssetsClient = Objects.requireNonNull(partyAssetsClient, "PartyAssetsClient must not be null");
    }

    public ResponseEntity checkPermit(String partyId, String municipalityId, String assetType) {
        partyId = StringUtils.trimToEmpty(partyId);
        municipalityId = StringUtils.trimToEmpty(municipalityId);

        if (!partyId.matches(PARTY_ID_REGEX) || !municipalityId.matches(MUNICIPALITY_ID_REGEX)) {
            logger.error("Invalid partyId or municipalityId: {}, {}", partyId, municipalityId);
            return util.buildPrecheckResponseEntity(HttpStatus.BAD_REQUEST, assetType, false, "Invalid data in the request");
        }

        if (StringUtils.isEmpty(partyId) || StringUtils.isEmpty(municipalityId)) {
            logger.error("Empty parameters in the request, partyId: {}, municipalityId: {}", partyId, municipalityId);
            return util.buildPrecheckResponseEntity(HttpStatus.BAD_REQUEST, assetType, false, "Invalid data in the request");
        }


        var citizen = citizenClient.getCitizen(partyId);
        var party = partyAssetsClient.getPartyAssets(partyId, String.valueOf(Status.ACTIVE));

        if (citizen.getStatusCode().isError() || party.getStatusCode().isError() || citizen.getStatusCode() == HttpStatus.NO_CONTENT || party.getStatusCode() == HttpStatus.NO_CONTENT) {
            logger.error("Resource not found for partyId: {}", partyId);
            return util.buildPrecheckResponseEntity(HttpStatus.NOT_FOUND, assetType, false, "Resource not found or resulted in an error");
        }

        if (!util.compareAllMunicipalityIds(Objects.requireNonNull(citizen.getBody(), "Citizen body must not be null"))) {
            logger.info("No valid Municipality ID found during check for partyId: {}", partyId);
            return util.buildPrecheckResponseEntity(HttpStatus.NOT_FOUND, assetType, false, "Municipality ID not found in the request");
        }

        if (StringUtils.isEmpty(assetType)) {
            var partyBody = Objects.requireNonNull(party.getBody(), "Party body must not be null");
            var permits = util.convertAssetsToPermitListObjects(partyBody);
            logger.info("Permit list retrieved successfully for partyId: {}", partyId);
            return ResponseEntity.ok().body(PermitListResponse.builder()
                    .withPartyId(partyId)
                    .withPermits(permits)
                    .build());
        }
        logger.info("Permit check successful for partyId: {}", partyId);
        return util.buildPrecheckResponseEntity(HttpStatus.OK, assetType, true, "");
    }
}