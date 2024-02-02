package se.sundsvall.precheck.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

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
@Schema(description = "Permit model.", accessMode = READ_ONLY)
public class Permit {

	@Schema(description = "The type of permit.", example = "PARKING_PERMIT", accessMode = READ_ONLY)
	private String type;

	@Schema(description = "If the permit is orderable or not.", accessMode = READ_ONLY)
	private boolean orderable;

	@Schema(description = "Extra message about the return data, for instance why the permit is not possible to order.", accessMode = READ_ONLY)
	private String reason;
}
