package se.sundsvall.precheck.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;
import se.sundsvall.precheck.api.model.PreCheckResponse;
import se.sundsvall.precheck.service.PreCheckService;

@RestController
@RequestMapping("/precheck")
@Tag(name = "Pre-check", description = "Pre-check operations")
@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = { Problem.class, ConstraintViolationProblem.class })))
@ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
public class PreCheckResource {

	private final PreCheckService preCheckService;

	public PreCheckResource(PreCheckService preCheckService) {
		this.preCheckService = preCheckService;
	}

	@GetMapping(path = "/{partyId}", produces = { APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE })
	@Operation(summary = "Fetch permit(s) issued by the provided municipality to the citizen matching the provided partyId")
	@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<PreCheckResponse> fetchPermits(
		@Parameter(name = "partyId", description = "PartyId for the citizen", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") @PathVariable @ValidUuid final String partyId,
		@Parameter(name = "municipalityId", description = "MunicipalityId for the citizen", example = "2281") @RequestParam @ValidMunicipalityId final String municipalityId,
		@Parameter(name = "assetType", description = "AssetType to filter on (optional)", example = "PARKING_PERMIT", allowEmptyValue = true) @RequestParam(required = false) final String assetType) {

		return ResponseEntity.ok(preCheckService.fetchPermits(partyId, municipalityId, assetType));
	}
}
