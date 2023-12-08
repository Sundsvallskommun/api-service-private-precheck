package se.sundsvall.precheck.service.utils;

import generated.client.citizen.CitizenAddress;
import generated.client.citizen.CitizenExtended;
import generated.client.partyAssets.Asset;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.zalando.problem.Problem;
import se.sundsvall.precheck.api.model.PreCheckResponse;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.zalando.problem.Status.BAD_REQUEST;
import static se.sundsvall.precheck.constant.Constants.ASSET_TYPE_EXISTS_ERROR_MESSAGE;
import static se.sundsvall.precheck.constant.Constants.CORRECT_ADDRESS_TYPE;
import static se.sundsvall.precheck.constant.Constants.NO_VALID_MUNICIPALITY_ID_FOUND;

public final class PreCheckUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(PreCheckUtil.class);

    private PreCheckUtil() {
        throw new IllegalStateException("PreCheckUtil should not be instantiated");
    }

    public static boolean checkResourceAvailability(ResponseEntity<CitizenExtended> citizen, ResponseEntity<List<Asset>> party) {
        if (citizen == null || party == null) {
            LOGGER.error("Citizen or Party is null during resource availability check");
            return true;
        }

        try {
            LOGGER.info("Input data checkResourceAvailability: Citizen: {}    Party: {}", citizen.getStatusCode(), party.getStatusCode());

            boolean isCitizenBodyPresent = citizen.getBody() != null && !citizen.getBody().equals(new CitizenExtended());
            boolean areStatusCodesAcceptable = citizen.getStatusCode() == HttpStatus.OK && party.getStatusCode() == HttpStatus.OK;

            if (isCitizenBodyPresent && areStatusCodesAcceptable) {
                LOGGER.info("Both Citizen and PartyAssets are valid.");
                return false;
            }
            LOGGER.info("Either Citizen or PartyAssets is invalid.");
            return true;
        } catch (Exception e) {
            LOGGER.error("Error occurred while validating in checkResourceAvailability: {}", e.getMessage());
            return true;
        }
    }

    public static boolean containsValidMunicipalityId(ResponseEntity<CitizenExtended> citizenEntity, String municipalityId) {
        if (citizenEntity == null || citizenEntity.getBody() == null) {
            LOGGER.error("Citizen is null during municipality ID check");
            return false;
        }

        if (municipalityId == null || municipalityId.isEmpty()) {
            LOGGER.error("MunicipalityId is null or empty during municipality ID check");
            return false;
        }

        CitizenExtended citizen = citizenEntity.getBody();
        if (citizen == null || citizen.getAddresses() == null) {
            LOGGER.error("Citizen or addresses is null during municipality ID check");
            return false;
        }

        for (CitizenAddress citizenAddress : citizen.getAddresses()) {
            if (isCorrectCitizenAddress(citizenAddress, municipalityId)) {
                LOGGER.info("Municipality ID found during check");
                return true;
            }
        }

        throw Problem.valueOf(BAD_REQUEST, NO_VALID_MUNICIPALITY_ID_FOUND);
    }

    private static boolean isCorrectCitizenAddress(CitizenAddress citizenAddress, String municipalityId) {
        var citizenAddressType = citizenAddress.getAddressType();
        var citizenMunicipality = citizenAddress.getMunicipality();

        if (citizenAddressType == null || citizenMunicipality == null) {
            LOGGER.error("CitizenAddress is null during municipality ID check");
            return false;
        }

        if (!citizenAddressType.equals(CORRECT_ADDRESS_TYPE)) {
            return false;
        }

        LOGGER.info("Looped the citizenAddressType: {} , and the CORRECT_ADDRESS_TYPE: {}", citizenAddressType, CORRECT_ADDRESS_TYPE);
        LOGGER.info("Looped the citizenMunicipality: {} , and the municipalityId: {}", citizenMunicipality, municipalityId);

        return citizenMunicipality.equals(municipalityId);
    }

    public static ResponseEntity<List<PreCheckResponse>> generateAssetTypeResponses(final String assetType, final ResponseEntity<List<Asset>> party) {

        if (StringUtils.isEmpty(assetType)) {
            return ResponseEntity.ok(List.of(createPrecheckResponse("", false, "When searching for assetType, assetType must be provided")));
        }
        if (party.getBody() == null || party.getBody().isEmpty()) {
            return ResponseEntity.ok(List.of(createPrecheckResponse(assetType, true, "")));
        }

        return Optional.ofNullable(party.getBody())
                .map(assets -> {
                    boolean assetTypeExists = assets.stream().anyMatch(asset -> asset.getType().equals(assetType));

                    if (assetTypeExists) {
                        String errorMessage = String.format(ASSET_TYPE_EXISTS_ERROR_MESSAGE, assetType);
                        return ResponseEntity.ok(List.of(createPrecheckResponse(assetType, false, errorMessage)));
                    }

                    return ResponseEntity.ok(List.of(createPrecheckResponse(assetType, true, "")));
                })
                .orElse(ResponseEntity.ok(List.of(createPrecheckResponse(assetType, true, ""))));
    }

    public static ResponseEntity<List<PreCheckResponse>> generateNoAssetTypeResponses(ResponseEntity<List<Asset>> party) {
        List<Asset> partyBody = party.getBody();

        if (partyBody == null || partyBody.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        try {
            List<PreCheckResponse> preCheckResponses = partyBody.stream()
                    .map(asset -> createPrecheckResponse(asset.getType(), false, ""))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(preCheckResponses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public static PreCheckResponse createPrecheckResponse(String assetType, @NotNull boolean eligible, String message) {
        return PreCheckResponse.builder()
                .withAssetType(assetType)
                .withEligible(eligible)
                .withMessage(message)
                .build();
    }
}
