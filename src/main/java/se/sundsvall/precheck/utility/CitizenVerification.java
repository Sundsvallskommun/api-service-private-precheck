package se.sundsvall.precheck.utility;


import se.sundsvall.precheck.integration.Citizen.model.CitizenAddress;
import se.sundsvall.precheck.integration.Citizen.model.CitizenResponse;

public class CitizenVerification {

        private CitizenResponse citizenResponse;
        private CitizenAddress[] addresses;
        public CitizenVerification(CitizenResponse citizenResponse) {
            this.citizenResponse = citizenResponse;
            this.addresses = citizenResponse.getAddresses();
        }
        public boolean validateMunicipalityId(int municipalityId) {
            for (CitizenAddress address : addresses) {
                String municipalityIdFromAddress = address.getAddressType();
                if (municipalityIdFromAddress.equals(String.valueOf(municipalityId))) {
                    return true;
                }
            }
            return false;
        }

}
