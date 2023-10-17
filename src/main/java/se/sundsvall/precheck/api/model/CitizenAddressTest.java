package se.sundsvall.precheck.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class CitizenAddressTest {
    @Schema(description = "Status", example = "String")
    private String status;
    @Schema(description = "NrDate", example = "String")
    private String nrDate;
    @Schema(description = "RealEstateDescription", example = "String")
    private String realEstateDescription;
    @Schema(description = "Co", example = "String")
    private String co;
    @Schema(description = "Address", example = "String")
    private String address;
    @Schema(description = "AddressArea", example = "String")
    private String addressArea;
    @Schema(description = "AddressNumber", example = "String")
    private String addressNumber;
    @Schema(description = "AddressLetter", example = "String")
    private String addressLetter;
    @Schema(description = "AppartmentNumber", example = "String")
    private String appartmentNumber;
    @Schema(description = "PostalCode", example = "String")
    private String postalCode;
    @Schema(description = "City", example = "String")
    private String city;
    @Schema(description = "County", example = "String")
    private String county;
    @Schema(description = "Municipality", example = "String")
    private String municipality;
    @Schema(description = "Country", example = "String")
    private String country;
    @Schema(description = "Emigrated", example = "boolean")
    private boolean emigrated;
    @Schema(description = "AddressType", example = "String")
    private String addressType;
    @Schema(description = "XCoordLocal", example = "double")
    private double xCoordLocal;
    @Schema(description = "YCoordLocal", example = "double")
    private double yCoordLocal;

    public String getAddressType() {
        return addressType;
    }
}