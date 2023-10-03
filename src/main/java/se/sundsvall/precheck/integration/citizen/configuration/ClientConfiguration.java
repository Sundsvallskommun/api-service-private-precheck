package se.sundsvall.precheck.integration.citizen.configuration;

import feign.Contract;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {
    @Bean
    public Contract useFeignAnnotations() {
        return new Contract.Default();
    }
}
