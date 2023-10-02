package se.sundsvall.precheck.api.model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
@Data
@Builder(setterPrefix = "with")
public class Citizen {
    @Schema(description = "String")
    private String partyId;
    @Schema(description = "String")
    private String givenName;
    @Schema(description = "String")
    private String lastName;
    @Schema(description = "String")
    private String gender;
    @Schema(description = "String")
    private String civilStatus;
    @Schema(description = "String")
    private String nrDate;
    @Schema(description = "String")
    private String classified;
    @Schema(description = "String")
    private String protectedNR;
    @Schema(description = "addressItem")
    private AddressItem[] addressItem;
}
