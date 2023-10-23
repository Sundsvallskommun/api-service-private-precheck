package se.sundsvall.precheck.utils;

import generated.client.citizen.CitizenExtended;
import generated.client.partyAssets.Asset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import se.sundsvall.precheck.api.model.PermitListObject;
import se.sundsvall.precheck.api.model.PrecheckResponse;

import java.util.List;

public class PreCheckUtil {
    private static final Logger logger = LoggerFactory.getLogger(PreCheckUtil.class);

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

    // Convert a list of assets to an array of PermitListObjects
    public PermitListObject[] convertAssetsToPermitListObjects(List<Asset> partyAssets) {
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
    public ResponseEntity<PrecheckResponse> buildPrecheckResponseEntity(HttpStatus status, String assetType, boolean orderable, String message) {
        PrecheckResponse precheckResponse = PrecheckResponse.builder()
                .withAssetType(assetType)
                .withOrderable(orderable)
                .withMessage(message)
                .build();
        return ResponseEntity.status(status).body(precheckResponse);
    }
}