package se.sundsvall.precheck.integration.citizen.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import se.sundsvall.precheck.Application;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class CitizenPropertiesTest {

	@Autowired
	private CitizenProperties properties;

	@Test
	void testProperties() {
		assertThat(properties.connectTimeout()).isEqualTo(19);
		assertThat(properties.readTimeout()).isEqualTo(21);
	}
}
