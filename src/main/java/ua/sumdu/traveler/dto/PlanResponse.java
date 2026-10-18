package ua.sumdu.traveler.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import ua.sumdu.traveler.entity.TravelPlan;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PlanResponse(
        UUID id,
        String title,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal budget,
        String currency,
        @JsonProperty("is_public") boolean isPublic,
        Integer version,
        Instant createdAt,
        Instant updatedAt,
        @JsonInclude(JsonInclude.Include.NON_NULL) Integer locationCount,       // лише у списку планів
        @JsonInclude(JsonInclude.Include.NON_NULL) List<LocationResponse> locations) {  // лише в GET за id

    /** POST / PUT: план без локацій. */
    public static PlanResponse from(TravelPlan p) {
        return build(p, null, null);
    }

    /** GET /api/travel-plans: план + кількість локацій. */
    public static PlanResponse summary(TravelPlan p, long locationCount) {
        return build(p, (int) locationCount, null);
    }

    /** GET /api/travel-plans/{id}: план з локаціями. */
    public static PlanResponse withLocations(TravelPlan p) {
        List<LocationResponse> locs = p.getLocations().stream()
                .map(LocationResponse::from)
                .toList();
        return build(p, null, locs);
    }

    private static PlanResponse build(TravelPlan p, Integer count, List<LocationResponse> locs) {
        return new PlanResponse(
                p.getId(), p.getTitle(), p.getDescription(),
                p.getStartDate(), p.getEndDate(), p.getBudget(), p.getCurrency(),
                p.isPublicPlan(), p.getVersion(), p.getCreatedAt(), p.getUpdatedAt(),
                count, locs);
    }
}