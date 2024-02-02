package se.sundsvall.precheck.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

class PermitTest {

	@Test
	void testBean() {
		assertThat(Permit.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var orderable = true;
		final var reason = "reason";
		final var type = "type";

		final var bean = Permit.builder()
			.withOrderable(orderable)
			.withReason(reason)
			.withType(type)
			.build();

		assertThat(bean.isOrderable()).isEqualTo(orderable);
		assertThat(bean.getReason()).isEqualTo(reason);
		assertThat(bean.getType()).isEqualTo(type);
	}

	@Test
	void testNoDirtOnEmptyBeans() {
		assertThat(Permit.builder().build()).hasAllNullFieldsOrPropertiesExcept("orderable").hasFieldOrPropertyWithValue("orderable", false);
		assertThat(new Permit()).hasAllNullFieldsOrPropertiesExcept("orderable").hasFieldOrPropertyWithValue("orderable", false);
	}
}
