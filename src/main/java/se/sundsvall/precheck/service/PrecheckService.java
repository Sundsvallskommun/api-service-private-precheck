package se.sundsvall.precheck.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.sundsvall.precheck.integration.citizen.CitizenClient;
import se.sundsvall.precheck.integration.partyAssets.partyAssetsClient;
@Service
public class PrecheckService {
        @Autowired
        private CitizenClient citizenClient;

        @Autowired
        private partyAssetsClient partyAssetsClient;


        public String checkPermit(final String personId, final String assetType, final int municipalityId) {

                var citizen = citizenClient.getCitizen(personId);
                var partyAssets = partyAssetsClient.getPartyAssets(personId);

                //log the results of the calls
                System.out.println("Citizen: " + citizen);
                System.out.println("PartyAssets: " + partyAssets);

                var TMP_RESULT = "NOT ELIGIBLE";
                return TMP_RESULT;
        }
}
