package se.sundsvall.precheck.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import se.sundsvall.precheck.integration.citizen.CitizenClient;
import se.sundsvall.precheck.integration.partyAssets.PartyAssetsClient;

@Service
public class PrecheckService {
        @Autowired
        private CitizenClient citizenClient;

        @Autowired
        private PartyAssetsClient PartyAssetsClient;


        public ResponseEntity<String> checkPermit(final String personId, final String assetType, final int municipalityId) {
                System.out.println("CitizenSTART");
                var citizen = citizenClient.getCitizen(personId);
                System.out.println("PartySTART");
                var party = PartyAssetsClient.getPartyAssets(personId);

                System.out.println("Citizenino: " + citizen);
                System.out.println("Partyino: " + party);

                var TMP_RESULT = ResponseEntity.ok("OK");
                return TMP_RESULT;
        }
}
