package se.sundsvall.precheck.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import se.sundsvall.precheck.api.model.PreCheckResponse;
import se.sundsvall.precheck.api.model.Status;
import se.sundsvall.precheck.integration.citizen.CitizenIntegration;
import se.sundsvall.precheck.integration.partyassets.PartyAssetsIntegration;

import java.util.List;

import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.precheck.constant.Constants.INVALID_MUNICIPALITY_ID;
import static se.sundsvall.precheck.constant.Constants.NOT_FOUND_ERROR_MESSAGE;
import static se.sundsvall.precheck.service.utils.PreCheckUtil.checkResourceAvailability;
import static se.sundsvall.precheck.service.utils.PreCheckUtil.containsValidMunicipalityId;
import static se.sundsvall.precheck.service.utils.PreCheckUtil.generateAssetTypeResponses;
import static se.sundsvall.precheck.service.utils.PreCheckUtil.generateNoAssetTypeResponses;

@Service
public final class PreCheckService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PreCheckService.class);
    private final CitizenIntegration citizenIntegration;
    private final PartyAssetsIntegration partyAssetsIntegration;


    public PreCheckService(CitizenIntegration citizenIntegration, PartyAssetsIntegration partyAssetsIntegration) {
        this.citizenIntegration = citizenIntegration;
        this.partyAssetsIntegration = partyAssetsIntegration;
    }

    public ResponseEntity<List<PreCheckResponse>> checkPermit(String partyId, String municipalityId, final String assetType) {
        final var citizen = citizenIntegration.getCitizen(partyId);
        final var partyAssets = partyAssetsIntegration.getPartyAssets(partyId, Status.ACTIVE);

        if (checkResourceAvailability(citizen, partyAssets)) {
            LOGGER.error("Citizen or Party is null or had no content during resource availability check");
            throw Problem.valueOf(NOT_FOUND, String.format(NOT_FOUND_ERROR_MESSAGE, partyId));
        }

        if (!containsValidMunicipalityId(citizen, municipalityId)) {
            LOGGER.info("No valid Municipality ID found during the check for partyId: {}", partyId);
            throw Problem.valueOf(BAD_REQUEST, String.format(INVALID_MUNICIPALITY_ID, partyId));
        }

        if (!StringUtils.isEmpty(assetType)) {
            LOGGER.info("Didn't have any assetType");
            return generateAssetTypeResponses(assetType, partyAssets);
        }

        return generateNoAssetTypeResponses(partyAssets);
    }
}
