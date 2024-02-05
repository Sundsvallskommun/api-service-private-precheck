package se.sundsvall.precheck.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(setterPrefix = "with")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Schema(description = "Pre-check response model.", accessMode = READ_ONLY)
public class PreCheckResponse {

	@Schema(description = "Signal if party is a citizen of the municipality or not.", accessMode = READ_ONLY)
	private boolean municipalCitizen;

	@Schema(description = "List of permits issued to the party.", accessMode = READ_ONLY)
	private List<Permit> permits;
}
