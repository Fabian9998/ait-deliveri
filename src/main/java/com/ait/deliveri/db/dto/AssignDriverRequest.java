package com.ait.deliveri.db.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignDriverRequest {

	@Schema(description = "Base64 del PDF", example = "pdf")
	@NotBlank
	private String pdf;

	@Schema(description = "Base64 de la IMAGEN", example = "jpg")
	@NotBlank
	private String image;
}
