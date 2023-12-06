package se.sundsvall.precheck.api;

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
import org.zalando.problem.Problem;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;
import se.sundsvall.precheck.api.model.PreCheckResponse;
import se.sundsvall.precheck.service.PreCheckService;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

@RestController
@RequestMapping("/PreCheck")
@Tag(
        name = "Check Permit(s)",
        description = "Check if a partyId is eligible for applying for a permit"
)
@ApiResponse(
        responseCode = "200",
        description = "OK",
        content = @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = PreCheckResponse.class)
        )
)
@ApiResponse(
        responseCode = "404",
        description = "Not Found",
        content = @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Problem.class)
        )
)
@ApiResponse(
        responseCode = "403",
        description = "Forbidden",
        content = @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Problem.class)
        )
)
public class CheckPermitsResource {

    private final PreCheckService PRE_CHECK_SERVICE;

    public CheckPermitsResource(PreCheckService PRE_CHECK_SERVICE) {
        this.PRE_CHECK_SERVICE = PRE_CHECK_SERVICE;
    }

    @GetMapping(path = "/{partyId}", produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE})
    public ResponseEntity<List<PreCheckResponse>> checkPermit(
            @Parameter(
                    name = "partyId",
                    description = "PartyId for the citizen",
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
            ) @ValidUuid @PathVariable(name = "partyId") final String partyId,
            @Parameter(
                    name = "municipalityId",
                    description = "MunicipalityId for the citizen",
                    example = "2281"
            ) @ValidMunicipalityId final String municipalityId,
            @Parameter(
                    name = "assetType",
                    description = "AssetType for the citizen",
                    example = "PARKING_PERMIT",
                    allowEmptyValue = true
            ) final String assetType) {

        return PRE_CHECK_SERVICE.checkPermit(partyId, municipalityId, assetType);
    }
}
