package se.sundsvall.precheck.utils;

import generated.client.citizen.CitizenExtended;
import generated.client.partyAssets.Asset;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import se.sundsvall.precheck.api.model.PermitListObject;
import se.sundsvall.precheck.api.model.PermitListResponse;
import se.sundsvall.precheck.api.model.PreCheckResponse;

import java.util.List;
import java.util.Objects;

import static se.sundsvall.precheck.utils.Constants.MUNICIPALITY_ID_REGEX;
import static se.sundsvall.precheck.utils.Constants.PARTY_ID_REGEX;

public class PreCheckUtil {
    private static final Logger logger = LoggerFactory.getLogger(PreCheckUtil.class);

    //Validate partyId and municipalityId
    public boolean isValidIds(String partyId, String municipalityId) {
        return partyId.matches(PARTY_ID_REGEX) && municipalityId.matches(MUNICIPALITY_ID_REGEX)
                && !StringUtils.isEmpty(partyId) && !StringUtils.isEmpty(municipalityId);
    }

    public boolean resourceNotFound(ResponseEntity<?> citizen, ResponseEntity<?> party) {
        return citizen.getStatusCode().isError() || party.getStatusCode().isError() ||
                citizen.getStatusCode() == HttpStatus.NO_CONTENT || party.getStatusCode() == HttpStatus.NO_CONTENT;
    }

    public boolean hasValidMunicipalityId(ResponseEntity<CitizenExtended> citizenBody) {
        return compareAllMunicipalityIds(Objects.requireNonNull(citizenBody.getBody(), "Citizen body must not be null"));
    }

    // Check if all municipality IDs are the same
    public boolean compareAllMunicipalityIds(CitizenExtended citizen) {
        if (citizen == null) {
            logger.error("Citizen is null during municipality ID check");
            return false;
        }
        for (var citizenAddress : citizen.getAddresses()) {
            // Temporary reassignment to handle the fact that the municipality ID is not set to a valid ID in the dev citizen API
            String municipalityId = "POPULATION_REGISTRATION_ADDRESS";
            if (citizenAddress.getAddressType().equals(municipalityId)) {
                logger.info("Municipality ID found during check");
                return true;
            }
        }
        logger.info("Municipality ID not found during check");
        return false;
    }

    public ResponseEntity<PreCheckResponse> handleAssetType(String assetType, String partyBody) {
        if (partyBody.isEmpty()) {
            return buildPrecheckResponseEntity(HttpStatus.OK, assetType, true, "");
        } else {
            return buildPrecheckResponseEntity(HttpStatus.OK, assetType, false, "Permit already exists for the given partyId");
        }
    }

    public ResponseEntity<PermitListResponse> handleNoAssetType(ResponseEntity<List<Asset>> party , String partyId) {
        try {
            var permits = convertAssetsToPermitListObjects(Objects.requireNonNull(party.getBody()));
            return ResponseEntity.ok().body(PermitListResponse.builder()
                    .withPartyId(partyId)
                    .withPermits(permits)
                    .build());
        } catch (Exception e) {
            logger.error("An unexpected error occurred: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public static PermitListObject[] convertAssetsToPermitListObjects(List<Asset> partyAssets) {
        if(partyAssets.isEmpty()) {
            logger.error("Party assets list is empty or null");
            return new PermitListObject[0];
        }
        PermitListObject[] permitObjects = new PermitListObject[partyAssets.size()];
        for (int i = 0; i < permitObjects.length; i++) {
            Asset currentAsset = partyAssets.get(i);
            permitObjects[i] = PermitListObject.builder()
                    .withPermitId(currentAsset.getPartyId())
                    .withPermitAssetId(currentAsset.getAssetId())
                    .withOrigin(currentAsset.getOrigin())
                    .withPermitType(currentAsset.getType())
                    .withIssued(currentAsset.getIssued())
                    .withValidTo(currentAsset.getValidTo())
                    .withPermitStatus(currentAsset.getStatus())
                    .withPermitDescription(currentAsset.getDescription())
                    .withPermitAdditionalParameters(currentAsset.getAdditionalParameters())
                    .build();
        }
        return permitObjects;
    }

    // Build a ResponseEntity for the PreCheckResponse
    public ResponseEntity<PreCheckResponse> buildPrecheckResponseEntity(HttpStatus status, String assetType, boolean orderable, String message) {
        PreCheckResponse precheckResponse = PreCheckResponse.builder()
                .withAssetType(assetType)
                .withOrderable(orderable)
                .withMessage(message)
                .build();
        return ResponseEntity.status(status).body(precheckResponse);
    }

    public ResponseEntity<PreCheckResponse> handleInvalidIds(String partyId, String municipalityId, String assetType) {
        logger.error("Invalid partyId or municipalityId provided. PartyId: {}, MunicipalityId: {}", partyId, municipalityId);
        return buildPrecheckResponseEntity(HttpStatus.BAD_REQUEST, assetType, false, "Invalid data provided in the request");
    }
}