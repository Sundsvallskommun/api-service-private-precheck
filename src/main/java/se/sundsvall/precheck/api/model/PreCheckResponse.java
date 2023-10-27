package se.sundsvall.precheck.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Builder(setterPrefix = "with")
public class PreCheckResponse {
    @Schema(description = "The type of asset", example = "PERMIT")
    private String assetType;
    @Schema(description = "Orderable", example = "boolean")
    private boolean orderable;
    @Schema(description = "Extra message about the return data, for instance why Orderable was false", example = "String")
    private String message;
}
