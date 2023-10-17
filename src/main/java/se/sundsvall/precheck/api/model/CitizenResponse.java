package se.sundsvall.precheck.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Builder(setterPrefix = "with")
public class CitizenResponse {
    @Schema(description = "personId", example = "String")
    private String personId;
    @Getter
    @Schema(description = "givenname", example = "String")
    private String givenname;
    @Schema(description = "lastname", example = "String")
    private String lastname;
    @Schema(description = "gender", example = "String")
    private String gender;
    @Schema(description = "civilStatus", example = "String")
    private String civilStatus;
    @Schema(description = "nrDate", example = "String")
    private String nrDate;
    @Schema(description = "classified", example = "String")
    private String classified;
    @Schema(description = "protectedNR", example = "String")
    private String protectedNR;
    @Schema(description = "addresses")
    private se.sundsvall.precheck.integration.Citizen.model.CitizenAddressTest[] addresses;

    public se.sundsvall.precheck.integration.Citizen.model.CitizenAddressTest[] getAddresses() {
        return addresses;
    }

    public void setAddresses(se.sundsvall.precheck.integration.Citizen.model.CitizenAddressTest[] addresses) {
        this.addresses = addresses;
    }
}