package se.sundsvall.precheck.constant;

public class Constants {
    // Used in PreCheckService.java
    public static final String INVALID_MUNICIPALITY_ID = "The owner of the partyId '%s' is not registered in the municipality where the permit is requested";

    // Used in PreCheckUtil.java
    public static final String NOT_FOUND_ERROR_MESSAGE = "Citizen data for '%s' was not given or had no content during execution";
    public static final String CORRECT_ADDRESS_TYPE = "POPULATION_REGISTRATION_ADDRESS";
    public static final String NO_VALID_MUNICIPALITY_ID_FOUND = "The owner of the given partyId is not register in the municipality where the permit is requested";
    public static final String ASSET_TYPE_EXISTS_ERROR_MESSAGE = "PersonId already has a permit of type '%s' associated with it";


}
