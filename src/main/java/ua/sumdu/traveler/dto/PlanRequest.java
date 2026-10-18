package ua.sumdu.traveler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PlanRequest(
        @Positive Integer version,                       // обов'язкове лише для PUT (перевіряє сервіс)
        @NotBlank @Size(max = 200) String title,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        @PositiveOrZero @Digits(integer = 8, fraction = 2) BigDecimal budget,
        @Pattern(regexp = "^[A-Z]{3}$") String currency,
        @JsonProperty("is_public") Boolean isPublic) {
}

