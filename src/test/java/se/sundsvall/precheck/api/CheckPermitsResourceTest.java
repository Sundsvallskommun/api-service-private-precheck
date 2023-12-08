package se.sundsvall.precheck.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import se.sundsvall.precheck.api.model.PreCheckResponse;
import se.sundsvall.precheck.service.PreCheckService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class CheckPermitsResourceTest {

    private static final String PARTY_ID = "testPartyId";
    private static final String MUNICIPALITY_ID = "58917";
    private static final String ASSET_TYPE = "PARKING_PERMIT";
    private MockMvc mockMvc;
    @Mock
    private PreCheckService preCheckService;

    @BeforeEach
    void setUp() {
        preCheckService = Mockito.mock(PreCheckService.class);
        CheckPermitsResource checkPermitsResource = new CheckPermitsResource(preCheckService);
        mockMvc = MockMvcBuilders.standaloneSetup(checkPermitsResource).build();
    }

    @Test
    void getCitizenWithAssetType_ReturnOneAssetType() throws Exception {

        when(preCheckService.checkPermit(anyString(), anyString(), anyString()))
                .thenReturn(ResponseEntity.ok(Collections.singletonList(
                        PreCheckResponse.builder()
                                .withAssetType(ASSET_TYPE)
                                .withEligible(false)
                                .withMessage("")
                                .build()
                )));

        mockMvc.perform(MockMvcRequestBuilders.get("/PreCheck/{partyId}", PARTY_ID)
                        .param("municipalityId", MUNICIPALITY_ID)
                        .param("assetType", ASSET_TYPE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].assetType").value("PARKING_PERMIT"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].eligible").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message").value(""));


        verify(preCheckService, times(1)).checkPermit(anyString(), anyString(), anyString());
    }

    @Test
    void getCitizenWithAssetType_ReturnMultipleAssetTypes() throws Exception {

        when(preCheckService.checkPermit(anyString(), anyString(), anyString()))
                .thenReturn(ResponseEntity.ok(List.of(
                        PreCheckResponse.builder()
                                .withAssetType("PARKING_PERMIT")
                                .withEligible(false)
                                .withMessage("")
                                .build(),
                        PreCheckResponse.builder()
                                .withAssetType("DRIVER_LICENSE")
                                .withEligible(false)
                                .withMessage("")
                                .build()
                )));

        mockMvc.perform(MockMvcRequestBuilders.get("/PreCheck/{partyId}", PARTY_ID)
                        .param("municipalityId", MUNICIPALITY_ID)
                        .param("assetType", "")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));

        verify(preCheckService, times(1)).checkPermit(PARTY_ID, MUNICIPALITY_ID, "");
    }
}