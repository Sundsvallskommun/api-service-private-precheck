package se.sundsvall.precheck.api;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;
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
        @GetMapping(path = "/{partyId}", produces = { APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE })
        @Operation(summary = "Api for checking if a partyId is eligible for applying for permit")
        @Tag(name = "precheck", description = "The precheck API for citizens and CitizenAssets")
        // API 2.xx response
        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = PrecheckResponse.class)))
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(defaultValue = APPLICATION_PROBLEM_JSON_VALUE)))
        @ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(defaultValue = APPLICATION_PROBLEM_JSON_VALUE)))
        public PrecheckResponse CheckPermit(
                @Parameter(name = "partyId", description = "PartyId for the citizen", example = "123") @Validated @PathVariable(name = "partyId") final String partyId,
                @Parameter(name = "assetType", description = "AssetType for the citizen", example = "PARKING_PERMIT") final String assetType,
                @Parameter(name = "municipalityId", description = "MunicipalityId for the citizen", example = "2555") final int municipalityId
        ) {
                return precheckService.checkPermit(partyId, assetType, municipalityId);
        }
}
