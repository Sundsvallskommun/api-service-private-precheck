package se.sundsvall.precheck.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder(setterPrefix = "with")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PreCheckResponse {
    @Schema(description = "The type of asset", example = "PERMIT")
    private String assetType;
    @Schema(description = "If the permit(s) are eligible", example = "boolean")
    private boolean eligible;
    @Schema(description = "Extra message about the return data, for instance why Orderable was false", example = "String")
    private String message;
}
