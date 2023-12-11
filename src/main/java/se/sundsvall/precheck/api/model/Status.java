package se.sundsvall.precheck.api.model;

import lombok.Getter;

@Getter
public enum Status {

    ACTIVE("ACTIVE"),

    EXPIRED("EXPIRED"),

    BLOCKED("BLOCKED");

    private final String value;

    Status(String value) {
        this.value = value;
    }

}



