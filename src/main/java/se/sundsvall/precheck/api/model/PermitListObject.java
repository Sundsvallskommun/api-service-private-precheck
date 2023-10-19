package se.sundsvall.precheck.api.model;

import generated.client.partyAssets.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Map;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Builder(setterPrefix = "with")
public class PermitListObject {
    private String permitId; // May be removed at a later date, might be useless for the client to know
    private String permitAssetId;
    private String origin;
    private String permitType;

    private LocalDate issued;
    private LocalDate validTo;

    private Status permitStatus;
    private String permitDescription;
    private Map<String, String> permitAdditionalParameters;
}


