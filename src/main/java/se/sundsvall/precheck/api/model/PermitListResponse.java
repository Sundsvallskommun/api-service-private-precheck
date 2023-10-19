package se.sundsvall.precheck.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Builder(setterPrefix = "with")
public class PermitListResponse {
    private final String partyId;

    private final String municipalityId;

    private final PermitListObject[] permits;
}

