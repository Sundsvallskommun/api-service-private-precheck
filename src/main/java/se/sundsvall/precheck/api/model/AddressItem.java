package se.sundsvall.precheck.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
@Data
@Builder(setterPrefix = "with")
public class AddressItem {
    @Schema(description = "String")
    private String Status;
    @Schema(description = "2023-10-02T06:52:20.196Z")
    private String nrDate;
    @Schema(description = "String")
    private String realEstateDescription;
    @Schema(description = "String")
    private String co;
    @Schema(description = "String")
    private String address;
    @Schema(description = "String")
    private String addressArea;
    @Schema(description = "String")
    private String addressNumber;
    @Schema(description = "String")
    private String addressLetter;
    @Schema(description = "String")
    private String appartmentNumber;
    @Schema(description = "String")
    private String postalCode;
    @Schema(description = "String")
    private String city;
    @Schema(description = "String")
    private String county;
    @Schema(description = "String")
    private String municipality;
    @Schema(description = "String")
    private String country;
    @Schema(description = "false/true")
    private boolean emigrated;
    @Schema(description = "String")
    private String addressType;
    @Schema(description = "float")
    private float xCoordLocal;
    @Schema(description = "float")
    private float yCoordLocal;
}
