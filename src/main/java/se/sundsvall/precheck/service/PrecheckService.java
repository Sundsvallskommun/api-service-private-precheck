package se.sundsvall.precheck.service;

import generated.client.citizen.CitizenExtended;
import generated.client.partyAssets.Asset;
import generated.client.partyAssets.Status;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import se.sundsvall.precheck.api.model.PermitListObject;
import se.sundsvall.precheck.api.model.PermitListResponse;
import se.sundsvall.precheck.api.model.PrecheckResponse;
import se.sundsvall.precheck.integration.citizen.CitizenClient;
import se.sundsvall.precheck.integration.partyAssets.PartyAssetsClient;

import java.util.List;
import java.util.Objects;

@Service
public class PrecheckService {
        private final CitizenClient citizenClient;
        private final PartyAssetsClient partyAssetsClient;

        @Autowired
        public PrecheckService(CitizenClient citizenClient, PartyAssetsClient partyAssetsClient) {
                this.citizenClient = citizenClient;
                this.partyAssetsClient = partyAssetsClient;
        }


        public ResponseEntity checkPermit(String partyId, String municipalityId, String assetType ) {
                partyId = StringUtils.trimToEmpty(partyId);
                municipalityId = StringUtils.trimToEmpty(municipalityId);

                if (partyId == null || partyId.isEmpty()) {
                        return new ResponseEntity<PrecheckResponse>(
                                PrecheckResponse.builder()
                                        .withAssetType(assetType)
                                        .withOrderable(false)
                                        .withMessage("Invalid data in request")

                                        .build(), HttpStatus.BAD_REQUEST);
                }

                var citizen = citizenClient.getCitizen(partyId);
                var party = partyAssetsClient.getPartyAssets(partyId, String.valueOf(Status.ACTIVE));

                if (citizen == null || citizen.getStatusCode().isError() || party.getStatusCode().isError() || citizen.getStatusCode().isSameCodeAs(HttpStatus.valueOf(204))) {
                        return new ResponseEntity<PrecheckResponse>(
                                PrecheckResponse.builder()
                                        .withAssetType(assetType)
                                        .withOrderable(false)
                                        .withMessage("Resource not found or resulted in an error") // TODO add a better message
                                        .build(), HttpStatus.NOT_FOUND);
                }
                if (!checkMunicipalityId(citizen.getBody(), municipalityId)) {  //if no municipalityId is found, return not found
                        return new ResponseEntity<PrecheckResponse>(
                                PrecheckResponse.builder()
                                        .withAssetType(assetType)
                                        .withOrderable(false)
                                        .withMessage("Invalid data in request")
                                        .build(), HttpStatus.NOT_FOUND
                        );
                }

                if (assetType == null || assetType.isEmpty()) {
                        var permits = extractPermits(Objects.requireNonNull(party.getBody()));
                        return new ResponseEntity<PermitListResponse>(
                                PermitListResponse.builder()
                                        .withPartyId(partyId)
                                        .withMunicipalityId(municipalityId)
                                        .withPermits(permits)
                                        .build(), HttpStatus.OK
                        );
                }

                if (party.getBody().isEmpty()) {
                        return new ResponseEntity<PrecheckResponse>(
                                PrecheckResponse.builder()
                                        .withAssetType(assetType)
                                        .withOrderable(true)
                                        .withMessage("")
                                        .build(), HttpStatus.OK
                        );
                }

                return new ResponseEntity<PrecheckResponse>(
                        PrecheckResponse.builder()
                                .withAssetType(assetType)
                                .withOrderable(true)
                                .withMessage("")
                                .build(), HttpStatus.OK
                );
        }


        private PrecheckResponse precheckResponseBuilder(String assetType, boolean orderable, String message) {
                return PrecheckResponse.builder()
                        .withAssetType(assetType)
                        .withOrderable(orderable)
                        .withMessage(message)
                        .build();

        }
        private boolean checkMunicipalityId(CitizenExtended citizen, String municipalityId) {
                if(citizen == null){
                        System.out.println("CheckMunicipalityId: citizen is null");
                        return false;
                }
                for (var citizenAsset : citizen.getAddresses()) {

                        if (citizenAsset.getAddressType().equals(municipalityId)) {
                                System.out.println("CheckMunicipalityId: municipalityId found");
                                return true;
                        }
                }
                System.out.println("CheckMunicipalityId: municipalityId not found");
                return false;
        }

        private PermitListObject[] extractPermits(List<Asset> partyContent) {
                PermitListObject[] permitObjects = new PermitListObject[partyContent.size()];
                System.out.println("ExtractPermits: partyContent size:"+partyContent.size());
                for (int i = 0; i < permitObjects.length; i++){
                        System.out.println("ExtractPermits: loop:"+i );
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


}
