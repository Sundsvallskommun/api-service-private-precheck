package se.sundsvall.precheck.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.sundsvall.precheck.api.PecheckResource;
import se.sundsvall.precheck.api.model.PrecheckResponse;

import java.util.List;

@Service
public class PrecheckService {

        @Value("${spring.application.url}")
        private String url;
        public List<PrecheckResponse> checkPermit(String partyId, String assetType, String municipalityId) {
                //print application.properties url to console
                System.out.println("url: " + url);

                return List.of(
                        PrecheckResponse.builder()
                                .withAssetType(assetType)
                                .withOrderable(true)
                                .withReason("OK")
                                .build()
                );
        }

}
