package se.sundsvall.precheck.utils;

import integrations.client.citizen.CitizenExtended;
import integrations.client.partyAssets.Asset;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.zalando.problem.Problem;
import se.sundsvall.precheck.api.model.PreCheckResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.zalando.problem.Status.FORBIDDEN;

public class PreCheckUtil {
    private static final Logger logger = LoggerFactory.getLogger(PreCheckUtil.class);
    private static final String NO_VALID_MUNICIPALITY_ID_FOUND = "The owner of the partyId given is not register in the municipality where the permit is requested"; //TODO: Fix a better message
    private static final String CORRECT_ADDRESS_TYPE = "POPULATION_REGISTRATION_ADDRESS";

    public static boolean resourceNotFound(ResponseEntity<CitizenExtended> citizen, ResponseEntity<List<Asset>> party) {
        logger.error("Input data resourceNotFound: "+citizen + "    "+ party);
        try {
            return citizen.getStatusCode().isError() || party.getStatusCode().isError() ||
                    citizen.getStatusCode() == HttpStatus.NO_CONTENT || party.getStatusCode() == HttpStatus.NO_CONTENT;
        } catch (Exception e) {
            logger.error("Error occurred while validating in resourceNotFound : " + e.getMessage());
            return true;
        }
    }

    // Check if all municipality IDs are the same
    public static ResponseEntity<List<PreCheckResponse>> handleNoGivenAssetType(ResponseEntity<List<Asset>> party) {
        List<PreCheckResponse> preCheckResponses = new ArrayList<>(); // Initialize the list
        for (var asset : Objects.requireNonNull(party.getBody())) {
            preCheckResponses.add(PreCheckResponse.builder()
                    .withAssetType(asset.getType())
                    .withOrderable(false)
                    .withMessage("")
                    .build());
        }
        return ResponseEntity.status(HttpStatus.OK).body(preCheckResponses);
    }

    public static boolean hasValidMunicipalityId(ResponseEntity<CitizenExtended> citizenEntity, String municipalityId) {
        var citizen = citizenEntity.getBody();
        logger.info("Citizen body: "+citizen);
        if (citizen == null || municipalityId == null || municipalityId.isEmpty()) {
            logger.error("Citizen or MunicipalityId is null during municipality ID check");
            return false;
        }
        for (var citizenAddress : citizen.getAddresses()) {
            if (!citizenAddress.getAddressType().equals(CORRECT_ADDRESS_TYPE)) {
                continue;
            }
            logger.info("Looped the citizen: " + citizenAddress.getAddressType() + " != " + CORRECT_ADDRESS_TYPE + " == " + (citizenAddress.getAddressType().equals(CORRECT_ADDRESS_TYPE)));
            logger.info("municipalityId loop " + citizenAddress.getMunicipality().equals(municipalityId) + " ==  " + citizenAddress.getMunicipality() + municipalityId);
            if (citizenAddress.getMunicipality().equals(municipalityId)) {
                logger.info("Municipality ID found during check");
                return true;
            }
        }

        throw Problem.valueOf(FORBIDDEN, String.format(NO_VALID_MUNICIPALITY_ID_FOUND));
    }

    public static ResponseEntity<List<PreCheckResponse>> handleAssetType(String assetType, ResponseEntity<List<Asset>> party) {
        List<Asset> partyBody = party.getBody();
        if (Objects.requireNonNull(partyBody).isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(List.of(buildPrecheckResponse(assetType, false, "")));
        }

        for (var asset : partyBody) {
            if (asset.getType().equals(assetType)) {
                return ResponseEntity.status(HttpStatus.OK).body(List.of(buildPrecheckResponse(assetType, false, String.format("PersonId already have a permit of type '%s' associated with it", assetType))));
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(List.of(buildPrecheckResponse(assetType, true, "")));
    }


    // Build a ResponseEntity for the PreCheckResponse
    public static PreCheckResponse buildPrecheckResponse( String assetType, @NotNull boolean orderable, String message) {
        return  PreCheckResponse.builder()
                .withAssetType(assetType)
                .withOrderable(orderable)
                .withMessage(message)
                .build();
    }

    public static ResponseEntity<List<Serializable>> handleInvalidIds(String partyId, String municipalityId, String assetType) {
        logger.error("Invalid partyId or municipalityId provided. PartyId: {}, MunicipalityId: {}", partyId, municipalityId);
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(List.of(assetType, false, "Invalid data provided in the request"));
    }
}