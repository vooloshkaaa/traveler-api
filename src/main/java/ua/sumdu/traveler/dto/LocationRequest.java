package ua.sumdu.traveler.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;

public record LocationRequest(
        @Positive Integer version,                       // обов'язкове лише для PUT
        @Size(max = 200) String name,                    // обов'язковість для POST перевіряє сервіс
        String address,
        @DecimalMin("-90")  @DecimalMax("90")  @Digits(integer = 2, fraction = 6) BigDecimal latitude,
        @DecimalMin("-180") @DecimalMax("180") @Digits(integer = 3, fraction = 6) BigDecimal longitude,
        @Positive Integer visitOrder,                    // необов'язкове
        Instant arrivalDate,
        Instant departureDate,
        @PositiveOrZero @Digits(integer = 8, fraction = 2) BigDecimal budget,
        String notes) {
}