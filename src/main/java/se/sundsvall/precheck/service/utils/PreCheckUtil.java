package se.sundsvall.precheck.service.utils;

import generated.client.citizen.CitizenAddress;
import generated.client.citizen.CitizenExtended;
import generated.client.partyAssets.Asset;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.zalando.problem.Problem;
import se.sundsvall.precheck.api.model.PreCheckResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.zalando.problem.Status.FORBIDDEN;

public class PreCheckUtil {
    public static final String CORRECT_ADDRESS_TYPE = "POPULATION_REGISTRATION_ADDRESS";
    private static final Logger logger = LoggerFactory.getLogger(PreCheckUtil.class);
    private static final String NO_VALID_MUNICIPALITY_ID_FOUND = "The owner of the partyId given is not register in the municipality where the permit is requested"; //TODO: Fix a better message

    public static boolean resourceNotFound(ResponseEntity<CitizenExtended> citizen, ResponseEntity<List<Asset>> party) {
        logger.info("Input data resourceNotFound: Citizen:" + citizen.getStatusCode() + "    Party:" + party.getStatusCode());
        try {
            return !(citizen.getStatusCode() == HttpStatus.OK && party.getStatusCode() == HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while validating in resourceNotFound : \n" + e.getMessage());
            return true;
        }
    }

    public static boolean hasInValidMunicipalityId(ResponseEntity<CitizenExtended> citizenEntity, String municipalityId) {
        var citizen = citizenEntity.getBody();

        if (citizen == null || citizen.getAddresses() == null || municipalityId == null || municipalityId.isEmpty()) {
            logger.error("Citizen or MunicipalityId is null during municipality ID check");
            return true;
        }

        for (var citizenAddress : citizen.getAddresses()) {
            if (isValidCitizenAddress(citizenAddress, municipalityId)) {
                logger.info("Municipality ID found during check");
                return false;
            }
        }

        throw Problem.valueOf(FORBIDDEN, NO_VALID_MUNICIPALITY_ID_FOUND);
    }

    private static boolean isValidCitizenAddress(CitizenAddress citizenAddress, String municipalityId) {
        var citizenAddressType = citizenAddress.getAddressType();
        var citizenMunicipality = citizenAddress.getMunicipality();

        if (citizenAddressType == null || citizenMunicipality == null) {
            logger.error("CitizenAddress is null during municipality ID check");
            return false;
        }

        if (!citizenAddressType.equals(CORRECT_ADDRESS_TYPE)) {
            return false;
        }

        logger.info("Looped the citizen: " + citizenAddressType + " != " + CORRECT_ADDRESS_TYPE + " == " + true);
        logger.info("municipalityId loop " + citizenMunicipality.equals(municipalityId) + " ==  " + citizenMunicipality + municipalityId);

        return citizenMunicipality.equals(municipalityId);
    }

    public static ResponseEntity<List<PreCheckResponse>> handleAssetType(String assetType, ResponseEntity<List<Asset>> party) {
        List<Asset> partyBody = party.getBody();

        // the difference between null and empty might be important, but I don't know, so I'll leave it as is until I know better - E.p
        if (partyBody == null || partyBody.isEmpty()) {
            boolean isNull = partyBody == null;
            String Message = isNull ? "No assets found for the given partyId" : "No content found for the given partyId";
            return ResponseEntity.ok(List.of(buildPrecheckResponse(assetType, !isNull, Message)));
        }

        boolean assetTypeExists = partyBody.stream()
                .anyMatch(asset -> asset.getType().equals(assetType));

        if (assetTypeExists) {
            String errorMessage = String.format("PersonId already has a permit of type '%s' associated with it", assetType);
            return ResponseEntity.ok(List.of(buildPrecheckResponse(assetType, false, errorMessage)));
        }

        return ResponseEntity.ok(List.of(buildPrecheckResponse(assetType, true, "")));
    }

    public static ResponseEntity<List<PreCheckResponse>> handleNoGivenAssetType(ResponseEntity<List<Asset>> party) {

        List<Asset> partyBody = party.getBody();
        if (partyBody == null || partyBody.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<PreCheckResponse> preCheckResponses = new ArrayList<>();
        for (var asset : Objects.requireNonNull(partyBody)) {
            preCheckResponses.add(buildPrecheckResponse(asset.getType(), false, ""));
        }
        return ResponseEntity.status(HttpStatus.OK).body(preCheckResponses);
    }

    public static PreCheckResponse buildPrecheckResponse(String assetType, @NotNull boolean eligible, String message) {
        return PreCheckResponse.builder()
                .withAssetType(assetType)
                .withEligible(eligible)
                .withMessage(message)
                .build();
    }
}
