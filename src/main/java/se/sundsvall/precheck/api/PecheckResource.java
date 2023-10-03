package se.sundsvall.precheck.api;


import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.media.Schema;
import se.sundsvall.precheck.api.model.AcctivePermitsResponse;
import se.sundsvall.precheck.api.model.List;
import se.sundsvall.precheck.api.model.PrecheckResponse;
import se.sundsvall.precheck.api.model.TestData;
import se.sundsvall.precheck.service.CitizenAssetsService;
import se.sundsvall.precheck.service.PrecheckService;

import java.util.logging.Logger;


@RestController
@Validated
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
        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(anyOf =  {PrecheckResponse.class, AcctivePermitsResponse.class})))
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema($schema = APPLICATION_PROBLEM_JSON_VALUE)))
        @ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema($schema = APPLICATION_PROBLEM_JSON_VALUE)))
        public TestData CheckPermit(
                @Parameter(name = "partyId", description = "PartyId for the citizen", example = "123") @Validated @PathVariable(name = "partyId") final String partyId,
                @Parameter(name = "assetType", description = "AssetType for the citizen", example = "PARKING_PERMIT") final String assetType,
                @Parameter(name = "municipalityId", description = "MunicipalityId for the citizen", example = "123-123-123-312") final String municipalityId
        ) {
                LOG.info("partyId: " + partyId + " assetType: " + assetType + " municipalityId: " + municipalityId);

                return CitizenAssetsService.checkPermit(partyId, assetType, municipalityId);

        }
}
