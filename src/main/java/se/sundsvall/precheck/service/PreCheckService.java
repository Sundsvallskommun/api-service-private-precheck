package se.sundsvall.precheck.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import se.sundsvall.precheck.api.model.PreCheckResponse;
import se.sundsvall.precheck.api.model.Status;
import se.sundsvall.precheck.integration.citizen.CitizenClient;
import se.sundsvall.precheck.integration.partyassets.PartyAssetsClient;

import java.util.List;
import java.util.Objects;

import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.precheck.constant.Constants.INVALID_MUNICIPALITY_ID;
import static se.sundsvall.precheck.constant.Constants.NOT_FOUND_ERROR_MESSAGE;
import static se.sundsvall.precheck.service.utils.PreCheckUtil.checkResourceAvailability;
import static se.sundsvall.precheck.service.utils.PreCheckUtil.containsValidMunicipalityId;
import static se.sundsvall.precheck.service.utils.PreCheckUtil.generateAssetTypeResponses;
import static se.sundsvall.precheck.service.utils.PreCheckUtil.generateNoAssetTypeResponses;

/**
 * The PreCheckService class is responsible for conducting permit-eligibility checks on a given partyId.
 * This class interfaces with external services, namely Citizen 2.0 and PartyAssets, to fetch essential data
 * necessary for the validation process.
 * It generates a list of one or more PreCheckResponse objects, depending on whether an assetType is specified or not.<p>
 * Methods:
 * <p>
 * - checkPermit: Performs checks for a permit based on the provided parameters.
 *               Returns a list of PreCheckResponse objects.
 *               Throws Problem if the pre-check fails.
 *
 * @param partyId           The ID of the party entity applying for a permit.
 * @param municipalityId    The ID of the municipality associated with the permit application.
 * @param assetType         The type of asset (e.g., PARKING_PERMIT), or an empty string if no asset type is specified.
 *
 * @return ResponseEntity   Containing a list of ether nothing or PreCheckResponse objects.
 * @throws Problem          If the pre-check fails, an appropriate Problem exception is thrown.
 */
@Service
public class PreCheckService {

    static final Logger LOGGER = LoggerFactory.getLogger(PreCheckService.class);
    private final CitizenClient CITIZEN_CLIENT;
    private final PartyAssetsClient PARTY_ASSETS_CLIENT;


    public PreCheckService(CitizenClient CITIZEN_CLIENT, PartyAssetsClient PARTY_ASSETS_CLIENT) {
        this.CITIZEN_CLIENT = Objects.requireNonNull(CITIZEN_CLIENT);
        this.PARTY_ASSETS_CLIENT = Objects.requireNonNull(PARTY_ASSETS_CLIENT);
    }

    public ResponseEntity<List<PreCheckResponse>> checkPermit(String partyId, String municipalityId, final String assetType) {
        final var citizen = CITIZEN_CLIENT.getCitizen(partyId);
        final var partyAssets = PARTY_ASSETS_CLIENT.getPartyAssets(partyId, String.valueOf(Status.ACTIVE));

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
