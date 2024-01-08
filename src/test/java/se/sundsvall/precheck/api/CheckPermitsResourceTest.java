package se.sundsvall.precheck.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import se.sundsvall.precheck.api.model.PreCheckResponse;
import se.sundsvall.precheck.service.PreCheckService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("junit")
@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class, controllers = CheckPermitsResource.class)
class CheckPermitsResourceTest {

    private static final String PARTY_ID = "7a1a2472-f100-437d-8702-f7dfb5e79efe";
    private static final String MUNICIPALITY_ID = "2281";
    private static final String ASSET_TYPE = "PARKING_PERMIT";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PreCheckService preCheckService;

    @Test
    void getCitizenWithAssetType_OneAssetType_ReturnsOK() throws Exception {

        when(preCheckService.checkPermit(anyString(), anyString(), anyString()))
                .thenReturn(List.of(
                        PreCheckResponse.builder()
                                .withAssetType(ASSET_TYPE)
                                .withEligible(false)
                                .withMessage("")
                                .build()
                ));

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
    void getCitizenWithAssetType_MultipleAssetTypes_ReturnsOk() throws Exception {

        when(preCheckService.checkPermit(anyString(), anyString(), anyString()))
                .thenReturn(List.of(
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
                ));

        mockMvc.perform(MockMvcRequestBuilders.get("/PreCheck/{partyId}", PARTY_ID)
                        .param("municipalityId", MUNICIPALITY_ID)
                        .param("assetType", "")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));

        verify(preCheckService, times(1)).checkPermit(PARTY_ID, MUNICIPALITY_ID, "");
    }

    @Test
    void getCitizenWithAssetType_ThrowsErrors() {
        when(preCheckService.checkPermit(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Test exception"));

        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/PreCheck/{partyId}", PARTY_ID)
                            .param("municipalityId", MUNICIPALITY_ID)
                            .param("assetType", "")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isInternalServerError());

            Assertions.fail("Should throw exception");
        } catch (Exception e) {
            assertEquals("Test exception", e.getCause().getMessage());
        }

        verify(preCheckService, times(1)).checkPermit(PARTY_ID, MUNICIPALITY_ID, "");
    }
}