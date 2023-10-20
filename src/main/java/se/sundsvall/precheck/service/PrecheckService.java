package se.sundsvall.precheck.service;


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

import  se.sundsvall.precheck.service.utils.Util;

import static se.sundsvall.precheck.service.utils.Constants.MUNICIPALITYID_REGEX;
import static se.sundsvall.precheck.service.utils.Constants.PARTYID_REGEX;

@Service
public class PrecheckService {
        private static final Logger logger = LoggerFactory.getLogger(PrecheckService.class);
        private final Util util = new Util();


        private final CitizenClient citizenClient;
        private final PartyAssetsClient partyAssetsClient;
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
                        return util.buildPrecheckResponseEntity(HttpStatus.BAD_REQUEST, assetType, false, "Invalid data in the request");
                }

                if (StringUtils.isEmpty(partyId)) {
                        return util.buildPrecheckResponseEntity(HttpStatus.BAD_REQUEST, assetType, false, "Invalid data in the request");
                }

                var citizen = citizenClient.getCitizen(partyId);
                var party = partyAssetsClient.getPartyAssets(partyId, String.valueOf(Status.ACTIVE));

                if (citizen.getStatusCode().isError() || party.getStatusCode().isError() || citizen.getStatusCode() == HttpStatus.NO_CONTENT || party.getStatusCode() == HttpStatus.NO_CONTENT) {
                        logger.error("Resource not found");
                        return util.buildPrecheckResponseEntity(HttpStatus.NOT_FOUND, assetType, false, "Resource not found or resulted in an error");
                }
                if (!util.comparingAllMunicipalityId(citizen.getBody())) {
                        logger.info("Municipality ID not found during check");
                        return util.buildPrecheckResponseEntity(HttpStatus.NOT_FOUND, assetType, false, "Invalid data in the request");
                }

                if (StringUtils.isEmpty(assetType)) {
                        var permits = util.convertAssetsToPermitListObjects(Objects.requireNonNull(party.getBody()));
                        return ResponseEntity.ok().body(PermitListResponse.builder()
                                .withPartyId(partyId)
                                .withMunicipalityId(municipalityId)
                                .withPermits(permits)
                                .build());
                }
                logger.info("Permit check successful");
                return util.buildPrecheckResponseEntity(HttpStatus.OK, assetType, true, "");
        }
}