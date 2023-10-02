package se.sundsvall.precheck.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import se.sundsvall.precheck.api.PecheckResource;
import se.sundsvall.precheck.api.model.PrecheckResponse;

import java.util.List;

@Service
public class PrecheckService {
        private static String citizenApi;
        public PrecheckService(@Value("${CITIZEN_API}") String citizenApi) {
                 this.citizenApi = citizenApi;
         }

        public static List<PrecheckResponse> checkPermit(String partyId, String assetType, String municipalityId) { //TODO LATER: Add the real api call here
            System.out.println("citizenApi: " + citizenApi);
            return List.of(
                    PrecheckResponse.builder()
                            .withAssetType(assetType)
                            .withOrderable(true)
                            .withReason("OK")
                            .build()
            );
        }

}
