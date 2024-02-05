package se.sundsvall.precheck.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class PreCheckResponseTest {

	@Test
	void testBean() {
		assertThat(PreCheckResponse.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var municipalCitizen = true;
		final var permits = List.of(Permit.builder().build());

		final var bean = PreCheckResponse.builder()
			.withMunicipalCitizen(municipalCitizen)
			.withPermits(permits)
			.build();

		assertThat(bean.isMunicipalCitizen()).isEqualTo(municipalCitizen);
		assertThat(bean.getPermits()).isEqualTo(permits);
	}

	@Test
	void testNoDirtOnEmptyBeans() {
		assertThat(PreCheckResponse.builder().build()).hasAllNullFieldsOrPropertiesExcept("municipalCitizen").hasFieldOrPropertyWithValue("municipalCitizen", false);
		assertThat(new PreCheckResponse()).hasAllNullFieldsOrPropertiesExcept("municipalCitizen").hasFieldOrPropertyWithValue("municipalCitizen", false);
	}
}
