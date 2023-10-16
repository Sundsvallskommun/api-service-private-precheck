package se.sundsvall.precheck.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Builder(setterPrefix = "with")
public class PrecheckResponse {
    @Schema(description = "The Status code", example = "200")
    private int Status;
    @Schema(description = "The type of asset", example = "PARKING_PERMIT")
    private String assetType;
    @Schema(description = "Orderable", example = "boolean")
    private boolean orderable = false;
    @Schema(description = "The reason if Orderable if false ", example = "String")
    private String reason;

    public PrecheckResponse from(final String assetType, final boolean orderable, final String reason) {
        return builder()
                .withAssetType(assetType)
                .withOrderable(orderable)
                .withReason(reason)
                .build();
    }
}
