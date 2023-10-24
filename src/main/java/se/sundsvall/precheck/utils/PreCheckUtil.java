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

public class PreCheckUtil {
    private static final Logger logger = LoggerFactory.getLogger(PreCheckUtil.class);
    public final static String PARTY_ID_REGEX = "^[0-9a-zA-Z]{8}-[0-9a-zA-Z]{4}-4[0-9a-zA-Z]{3}-[89abAB][0-9a-zA-Z]{3}-[0-9a-zA-Z]{12}$";
    public final static String MUNICIPALITY_ID_REGEX = "^[0-9]{4}$";

    //Validate partyId and municipalityId
    public static boolean isValidIds(String partyId, String municipalityId) {
        return partyId.matches(PARTY_ID_REGEX) && municipalityId.matches(MUNICIPALITY_ID_REGEX)
                && !StringUtils.isEmpty(partyId) && !StringUtils.isEmpty(municipalityId);
    }

    public static boolean resourceNotFound(ResponseEntity<?> citizen, ResponseEntity<?> party) {
        return citizen.getStatusCode().isError() || party.getStatusCode().isError() ||
                citizen.getStatusCode() == HttpStatus.NO_CONTENT || party.getStatusCode() == HttpStatus.NO_CONTENT;
    }

    // Check if all municipality IDs are the same
    public static boolean hasValidMunicipalityId(ResponseEntity<CitizenExtended> citizenEntity, String municipalityId) {
        var citizen = citizenEntity.getBody();
        if (citizen == null || municipalityId.isEmpty()) {
            logger.error("Citizen or MunicipalityId is null during municipality ID check");
            return false;
        }
        for (var citizenAddress : citizen.getAddresses()) {
            if (citizenAddress.getAddressType().equals(municipalityId)) {
                logger.info("Municipality ID found during check");
                return true;
            }
        }
        logger.info("Municipality ID not found during check");
        return false;
    }

    public static ResponseEntity<PreCheckResponse> handleAssetType(String assetType, String partyBody) {
        if (partyBody.isEmpty()) {
            return buildPrecheckResponseEntity(HttpStatus.OK, assetType, true, "");
        } else {
            return buildPrecheckResponseEntity(HttpStatus.OK, assetType, false, "'"+assetType+"' can't be ordered for the given partyId because other permits already exist for the partyId");
        }
    }

    public static ResponseEntity<PermitListResponse> handleNoGivenAssetType(ResponseEntity<List<Asset>> party , String partyId) {
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
    public static ResponseEntity<PreCheckResponse> buildPrecheckResponseEntity(HttpStatus status, String assetType, boolean orderable, String message) {
        PreCheckResponse precheckResponse = PreCheckResponse.builder()
                .withAssetType(assetType)
                .withOrderable(orderable)
                .withMessage(message)
                .build();
        return ResponseEntity.status(status).body(precheckResponse);
    }

    public static ResponseEntity<PreCheckResponse> handleInvalidIds(String partyId, String municipalityId, String assetType) {
        logger.error("Invalid partyId or municipalityId provided. PartyId: {}, MunicipalityId: {}", partyId, municipalityId);
        return buildPrecheckResponseEntity(HttpStatus.BAD_REQUEST, assetType, false, "Invalid data provided in the request");
    }
}