package se.sundsvall.precheck.integration.citizen;

import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestMethod;
import se.sundsvall.precheck.api.model.TestData;
import se.sundsvall.precheck.integration.citizen.configuration.ClientConfiguration;

import java.util.List;

@FeignClient(
        value = "${app.feign.config.name}",
        url = "${app.feign.config.url}",
        configuration = ClientConfiguration.class,
        dismiss404 = true
)
public interface CitizenClient {

    @RequestLine("GET /api")
    TestData getTestDataJson();


}
