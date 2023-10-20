package se.sundsvall.precheck.service;


import generated.client.citizen.CitizenExtended;
import generated.client.partyAssets.Asset;
import generated.client.partyAssets.Status;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        private static final Logger logger = LoggerFactory.getLogger(PrecheckService.class);
        private final CitizenClient citizenClient;
        private final PartyAssetsClient partyAssetsClient;

        private final static String PARTYID_REGEX = "^[0-9a-zA-Z]{8}-[0-9a-zA-Z]{4}-4[0-9a-zA-Z]{3}-[89abAB][0-9a-zA-Z]{3}-[0-9a-zA-Z]{12}$";
        private final static String MUNICIPALITYID_REGEX = "^[0-9]{4}$";
        @Autowired
        public PrecheckService(CitizenClient citizenClient, PartyAssetsClient partyAssetsClient) {
                this.citizenClient = citizenClient;
                this.partyAssetsClient = partyAssetsClient;
        }
        public ResponseEntity checkPermit(String partyId, String municipalityId, String assetType) {
                partyId = StringUtils.trimToEmpty(partyId);
                municipalityId = StringUtils.trimToEmpty(municipalityId);


                if (!partyId.matches(PARTYID_REGEX) || !municipalityId.matches(MUNICIPALITYID_REGEX)) {
                        logger.error("Invalid partyId or municipalityId");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(precheckResponseBuilder(assetType, false, "Invalid data in the request"));
                }

                if (StringUtils.isEmpty(partyId)) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(precheckResponseBuilder(assetType, false, "Invalid data in the request"));
                }

                var citizen = citizenClient.getCitizen(partyId);
                var party = partyAssetsClient.getPartyAssets(partyId, String.valueOf(Status.ACTIVE));

                if (citizen.getStatusCode().isError() || party.getStatusCode().isError() || citizen.getStatusCode() == HttpStatus.NO_CONTENT || party.getStatusCode() == HttpStatus.NO_CONTENT) {
                        logger.error("Resource not found");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(precheckResponseBuilder(assetType, false, "Resource not found or resulted in an error"));
                }
                if (!checkMunicipalityId(citizen.getBody())) {
                        logger.info("Municipality ID not found during check");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(precheckResponseBuilder(assetType, false, "Invalid data in the request"));
                }

                if (StringUtils.isEmpty(assetType)) {
                        var permits = extractPermits(Objects.requireNonNull(party.getBody()));
                        return ResponseEntity.ok().body(PermitListResponse.builder()
                                .withPartyId(partyId)
                                .withMunicipalityId(municipalityId)
                                .withPermits(permits)
                                .build());
                }

                logger.info("Permit check successful");
                return ResponseEntity.ok(precheckResponseBuilder(assetType, true, ""));
        }

        private PrecheckResponse precheckResponseBuilder(String assetType, boolean orderable, String message) {
                return PrecheckResponse.builder()
                        .withAssetType(assetType)
                        .withOrderable(orderable)
                        .withMessage(message)
                        .build();
        }

        private boolean checkMunicipalityId(CitizenExtended citizen) {
                if (citizen == null) {
                        logger.error("Citizen is null during municipality ID check");
                        return false;
                }
                for (var citizenAsset : citizen.getAddresses()) {
                        //TODO: REMOVE THIS REASSIGNMENT LATER
                        String municipalityId = "POPULATION_REGISTRATION_ADDRESS";
                        if (citizenAsset.getAddressType().equals(municipalityId)) {
                                logger.info("Municipality ID found during check");
                                return true;
                        }
                }
                logger.info("Municipality ID not found during check");
                return false;
        }

        private PermitListObject[] extractPermits(List<Asset> partyContent) {
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
}