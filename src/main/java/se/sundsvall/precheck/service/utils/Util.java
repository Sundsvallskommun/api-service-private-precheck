package se.sundsvall.precheck.service.utils;

import generated.client.citizen.CitizenExtended;
import generated.client.partyAssets.Asset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import se.sundsvall.precheck.api.model.PermitListObject;
import se.sundsvall.precheck.api.model.PrecheckResponse;

import java.util.List;

@Service
public class Util {
    private static final Logger log = LoggerFactory.getLogger(Util.class);

    public boolean comparingAllMunicipalityId(CitizenExtended citizen) {
        if (citizen == null) {
            log.error("Citizen is null during municipality ID check");
            return false;
        }
        for (var citizenAsset : citizen.getAddresses()) {
            //TODO: REMOVE THIS REASSIGNMENT LATER | It is here handel the fact that the municipality ID is not set to a valid ID in dev citizen api
            String municipalityId = "POPULATION_REGISTRATION_ADDRESS";
            if (citizenAsset.getAddressType().equals(municipalityId)) {
                log.info("Municipality ID found during check");
                return true;
            }
        }
        log.info("Municipality ID not found during check");
        return false;
    }


    public PermitListObject[] convertAssetsToPermitListObjects(List<Asset> partyContent) {
        PermitListObject[] permitObjects = new PermitListObject[partyContent.size()];
        for (int i = 0; i < permitObjects.length; i++) {
            permitObjects[i] = PermitListObject.builder()
                    .withPermitId(partyContent.get(i).getPartyId())
                    .withPermitAssetId(partyContent.get(i).getAssetId())
                    .withOrigin(partyContent.get(i).getOrigin())
                    .withPermitType(partyContent.get(i).getType())
                    .withIssued(partyContent.get(i).getIssued())
                    .withValidTo(partyContent.get(i).getValidTo())
                    .withPermitStatus(partyContent.get(i).getStatus())
                    .withPermitDescription(partyContent.get(i).getDescription())
                    .withPermitAdditionalParameters(partyContent.get(i).getAdditionalParameters())
                    .build();
        }
        return permitObjects;
    }

    public ResponseEntity buildPrecheckResponseEntity(HttpStatus status, String assetType, boolean orderable, String message) {
        return ResponseEntity.status(status).body(PrecheckResponse.builder()
                .withAssetType(assetType)
                .withOrderable(orderable)
                .withMessage(message)
                .build());
    }


}
