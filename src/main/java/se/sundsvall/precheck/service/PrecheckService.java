package se.sundsvall.precheck.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.sundsvall.precheck.api.model.PrecheckResponse;
import se.sundsvall.precheck.integration.citizen.CitizenClient;
import se.sundsvall.precheck.utility.CitizenVerification;

@Service
public class PrecheckService {
        @Autowired
        private CitizenClient citizenClient;

        private CitizenVerification citizenVerification = null;

        public PrecheckResponse checkPermit(final String personId, final String assetType, final int municipalityId){

                var response = citizenClient.getCitizen(personId);
                if(response.isEmpty()) { //Pr nt refers to if the Optional has a value present or not
                        return PrecheckResponse.builder()
                                .withStatus(404)
                                .withAssetType(assetType)
                                .withOrderable(false)
                                .withReason("Party not found")
                                .build();
                }


                try {
                        citizenVerification = new CitizenVerification(new ObjectMapper().readValue(response.get(), se.sundsvall.precheck.integration.Citizen.model.CitizenResponse.class));
                } catch (Exception e) {
                        e.printStackTrace();
                }

                if(citizenVerification.validateMunicipalityId(municipalityId) == false) {
                        return PrecheckResponse.builder()
                                .withStatus(200)
                                .withAssetType(assetType)
                                .withOrderable(false)
                                .withReason("Party is not registered in the municipality")
                                .build();
                }

                return PrecheckResponse.builder()
                        .withStatus(200)
                        .withAssetType("PARKING_PERMIT")
                        .withReason("OK")
                        .withOrderable(true)
                        .build();

        }

}
