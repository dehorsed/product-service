package org.dehorsed.productservice.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductRequest(
                @NotBlank String name,
                @NotBlank String description,
                @NotNull BigDecimal price) {
}
