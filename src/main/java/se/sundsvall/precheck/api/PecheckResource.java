package se.sundsvall.precheck.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;
import se.sundsvall.precheck.api.model.PermitListResponse;
import se.sundsvall.precheck.api.model.PrecheckResponse;
import se.sundsvall.precheck.service.PrecheckService;

import java.util.logging.Logger;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

@RestController
@ValidUuid
@RequestMapping("/precheck")
@Tag(name = "precheck", description = "The precheck API for citizens and CitizenAssets")
public class PecheckResource {


    private static final Logger LOG = Logger.getLogger(PecheckResource.class.getName());

    private final PrecheckService precheckService;

    public PecheckResource(PrecheckService precheckService) {
        this.precheckService = precheckService;
    }

    //anyOf = { "municipalityId", "assetType" }
    @GetMapping(path = "/{partyId}", produces = "application/json")
    @Operation(summary = "Api for checking if a partyId is eligible for applying for permit")
    @Tag(name = "precheck", description = "The precheck API for citizens and CitizenAssets")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(anyOf = {PrecheckResponse.class, PermitListResponse.class})))
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = PrecheckResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = PrecheckResponse.class)))
    public ResponseEntity CheckPermit(
            @Parameter(name = "partyId", description = "PartyId for the citizen", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") @ValidUuid @PathVariable(name = "partyId") final String partyId,
            @Parameter(name = "municipalityId", description = "MunicipalityId for the citizen", example = "2555") final String municipalityId,
            @Parameter(name = "assetType", description = "AssetType for the citizen", example = "PARKING_PERMIT", allowEmptyValue = true, required = false) final String assetType
    ) {
        return precheckService.checkPermit(partyId, municipalityId,assetType);
    }

}
