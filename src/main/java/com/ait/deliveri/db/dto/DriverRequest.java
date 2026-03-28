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
public class DriverRequest {

	@Schema(description = "Nombre del conductor", example = "Juan")
	@NotBlank
    private String name;

    @NotBlank
    @Schema(description = "Licencia del conductor", example = "1d-Ad123")
    private String licenseNumber;
}
