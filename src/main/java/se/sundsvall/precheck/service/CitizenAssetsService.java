package se.sundsvall.precheck.service;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import se.sundsvall.precheck.api.model.TestData;
import se.sundsvall.precheck.integration.citizen.CitizenClient;

import java.util.Collections;
import java.util.List;


@Service
public class CitizenAssetsService {

    @NotNull
    @Contract(pure = true)
    public static TestData checkPermit(String partyId, String assetType, String municipalityId) {
        CitizenClient citizenClient = Feign.builder()
                .decoder(new JacksonDecoder())
                .target(CitizenClient.class, "http://localhost:3000");
        System.out.println("citizenClient: " + citizenClient);

        TestData testData = citizenClient.getTestDataJson();
        return testData;

    }
}