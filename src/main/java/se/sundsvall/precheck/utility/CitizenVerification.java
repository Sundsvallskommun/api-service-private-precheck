package se.sundsvall.precheck.utility;


import se.sundsvall.precheck.integration.Citizen.model.CitizenResponse;

public class CitizenVerification {

        private CitizenResponse citizenResponse;
        private se.sundsvall.precheck.integration.Citizen.model.CitizenAddressTest[] addresses;
        public CitizenVerification(CitizenResponse citizenResponse) {
            this.citizenResponse = citizenResponse;
            this.addresses = citizenResponse.getAddresses();
        }
        public boolean validateMunicipalityId(int municipalityId) {
            for (se.sundsvall.precheck.integration.Citizen.model.CitizenAddressTest address : addresses) {
                String municipalityIdFromAddress = address.getAddressType();
                if (municipalityIdFromAddress.equals(String.valueOf(municipalityId))) {
                    return true;
                }
            }
            return false;
        }

}
