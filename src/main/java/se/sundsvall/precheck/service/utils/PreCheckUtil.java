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
import org.springframework.web.server.ResponseStatusException;
import org.zalando.problem.Problem;
import se.sundsvall.precheck.api.model.PreCheckResponse;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

        boolean isCitizenBodyPresent = citizen.getBody() != null;
        boolean areStatusCodesAcceptable = citizen.getStatusCode() == HttpStatus.OK && party.getStatusCode() == HttpStatus.OK;

        if (isCitizenBodyPresent && areStatusCodesAcceptable) {
            LOGGER.info("Both Citizen and PartyAssets are valid.");
            return false;
        } else {
            LOGGER.info("Either Citizen or PartyAssets is invalid.");
            return true;
        }
    }

    public static boolean containsValidMunicipalityId(ResponseEntity<CitizenExtended> citizenEntity, String municipalityId) {
        if (invalidInput(citizenEntity, municipalityId)) {
            return false;
        }

        CitizenExtended citizen = citizenEntity.getBody();

        if (citizen != null && citizen.getAddresses() != null) {
            for (CitizenAddress citizenAddress : citizen.getAddresses()) {
                if (isCorrectAddressTypeAndMunicipality(citizenAddress, municipalityId)) {
                    return true;
                }
            }
        }

        throw Problem.valueOf(BAD_REQUEST, NO_VALID_MUNICIPALITY_ID_FOUND);
    }



    private static boolean invalidInput(ResponseEntity<CitizenExtended> citizenEntity, String municipalityId) {
        if (citizenEntity == null || citizenEntity.getBody() == null ||
                municipalityId == null || municipalityId.isEmpty()) {
            LOGGER.error("Invalid input for municipality ID check");
            return true;
        }

        CitizenExtended citizen = citizenEntity.getBody();
        return citizen == null || citizen.getAddresses() == null;
    }

    private static boolean isCorrectAddressTypeAndMunicipality(CitizenAddress citizenAddress, String municipalityId) {
        var citizenAddressType = citizenAddress.getAddressType();
        var citizenMunicipality = citizenAddress.getMunicipality();

        if (citizenAddressType == null || citizenMunicipality == null) {
            LOGGER.error("Incomplete citizen address details during municipality ID check");
            return false;
        }

        if (!citizenAddressType.equals(CORRECT_ADDRESS_TYPE)) {
            return false;
        }

        return citizenMunicipality.equals(municipalityId);
    }


    public static List<PreCheckResponse> generateAssetTypeResponses(final String assetType, final ResponseEntity<List<Asset>> party) {
        if (StringUtils.isEmpty(assetType) || party == null) {
            return List.of(createPrecheckResponse("", false, "When searching for assetType, assetType must be provided"));
        }

        List<Asset> body = party.getBody();
        if (body == null || body.isEmpty()) {
            return List.of(createPrecheckResponse(assetType, true, ""));
        }

        return body.stream()
                .anyMatch(asset -> asset.getType() != null && asset.getType().equals(assetType))
                ? List.of(createPrecheckResponse(assetType, false, String.format(ASSET_TYPE_EXISTS_ERROR_MESSAGE, assetType)))
                : List.of(createPrecheckResponse(assetType, true, ""));

    }

    public static List<PreCheckResponse> generateNoAssetTypeResponses(ResponseEntity<List<Asset>> party) {

        List<Asset> partyBody;
        try {
            LOGGER.info("Input data generateNoAssetTypeResponses: Party: {}", party.getStatusCode());
            partyBody = Optional.ofNullable(party.getBody()).orElse(Collections.emptyList());
        } catch (Exception e) {
            LOGGER.error("Error occurred while validating in generateNoAssetTypeResponses: {}", e.getMessage());
            return Collections.emptyList();
        }

        List<PreCheckResponse> preCheckResponses;
        try {
            preCheckResponses = partyBody.stream()
                    .filter(Objects::nonNull)
                    .map(asset -> createPrecheckResponse(asset.getType(), false, ""))
                    .toList();

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating responses", e);
        }
        return preCheckResponses;
    }


    public static PreCheckResponse createPrecheckResponse(String assetType, @NotNull boolean eligible, String message) {
        return PreCheckResponse.builder()
                .withAssetType(assetType)
                .withEligible(eligible)
                .withMessage(message)
                .build();
    }
}
