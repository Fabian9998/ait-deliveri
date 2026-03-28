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
public class OrderRequest {

	@Schema(description = "Origen de la orden", example = "Av Palafox num24")
	@NotBlank
	private String origin;

	@Schema(description = "Destino de la orden", example = "Av Insurgetes num12")
	@NotBlank
	private String destination;

}
