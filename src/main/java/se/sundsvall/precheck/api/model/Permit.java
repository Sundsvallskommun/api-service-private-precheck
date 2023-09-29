package se.sundsvall.precheck.api.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.DateTimeException;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Builder(setterPrefix = "with")
public class Permit {

    @Schema(description = "The type of asset", example = "String")
    private String assetType;
    @Schema(description = "The date and time when the permit expires", example = "String")
    private String expires;
}
