package se.sundsvall.precheck.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Builder(setterPrefix = "with")
public class PermitListResponse {
    @Schema(description = "A list of all active permits", example = "{String, String, String}")
    private String[] permits;
}
