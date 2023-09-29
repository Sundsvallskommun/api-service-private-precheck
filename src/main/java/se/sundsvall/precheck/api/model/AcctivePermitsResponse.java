package se.sundsvall.precheck.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Builder(setterPrefix = "with")
public class AcctivePermitsResponse {

    @Schema(description = "The type of asset that are acctive", example = "String")
    private List<Permit> permits;

}



