package se.sundsvall.precheck.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Builder(setterPrefix = "with")
public class PrecheckResponse {
    @Schema(description = "The type of asset", example = "String")
    private String assetType;
    @Schema(description = "Orderable", example = "Boolean")
    private boolean orderable;
    @Schema(description = "The reason if Orderable is false ", example = "String")
    private String reason;
}
