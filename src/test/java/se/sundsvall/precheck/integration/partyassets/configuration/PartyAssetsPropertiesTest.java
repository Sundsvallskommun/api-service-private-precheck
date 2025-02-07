package se.sundsvall.precheck.integration.partyassets.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.precheck.Application;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class PartyAssetsPropertiesTest {

	@Autowired
	private PartyAssetsProperties properties;

	@Test
	void testProperties() {
		assertThat(properties.connectTimeout()).isEqualTo(18);
		assertThat(properties.readTimeout()).isEqualTo(22);
	}
}
