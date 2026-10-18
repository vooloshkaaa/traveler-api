package ua.sumdu.traveler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import ua.sumdu.traveler.entity.Location;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record LocationResponse(
        UUID id,
        @JsonProperty("travel_plan_id")
        UUID travelPlanId,
        String name,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        @JsonProperty("visit_order")
        Integer visitOrder,
        Instant arrivalDate,
        Instant departureDate,
        BigDecimal budget,
        String notes,
        Integer version,
        Instant createdAt) {

    public static LocationResponse from(Location l) {
        return new LocationResponse(
                l.getId(),
                l.getTravelPlan().getId(),      // для LAZY-зв'язку getId() не звертається до БД
                l.getName(), l.getAddress(),
                l.getLatitude(), l.getLongitude(), l.getVisitOrder(),
                l.getArrivalDate(), l.getDepartureDate(),
                l.getBudget(), l.getNotes(),
                l.getVersion(), l.getCreatedAt());
    }
}