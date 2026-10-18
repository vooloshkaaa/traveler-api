package ua.sumdu.traveler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import ua.sumdu.traveler.entity.Location;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface LocationRepository extends JpaRepository<Location, UUID> {

    @Query("select coalesce(max(l.visitOrder), 0) from Location l where l.travelPlan.id = :planId")
    int maxOrder(UUID planId);

    @Query("select count(l) from Location l where l.travelPlan.id = :planId")
    int countInPlan(UUID planId);

    @Query(nativeQuery = true, value = "SELECT travel_plan_id FROM locations WHERE id = :id")
    Optional<UUID> findPlanId(UUID id);

    @Query(nativeQuery = true, value = "SELECT visit_order FROM locations WHERE id = :id")
    Optional<Integer> findOrder(UUID id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(nativeQuery = true, value = """
    UPDATE locations SET
        name = COALESCE(CAST(:name AS text), name),
        address = COALESCE(CAST(:address AS text), address),
        latitude = COALESCE(CAST(:latitude AS numeric), latitude),
        longitude = COALESCE(CAST(:longitude AS numeric), longitude),
        arrival_date = COALESCE(CAST(:arrival AS timestamptz), arrival_date),
        departure_date = COALESCE(CAST(:departure AS timestamptz), departure_date),
        budget = COALESCE(CAST(:budget AS numeric), budget),
        notes = COALESCE(CAST(:notes AS text), notes)
    WHERE id = :id
    """)
    int updatePartial(UUID id, String name, String address, BigDecimal latitude, BigDecimal longitude,
                      Instant arrival, Instant departure, BigDecimal budget, String notes);

    /** Зсув діапазону порядкових номерів на delta (+1/-1). */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(nativeQuery = true, value = """
        UPDATE locations SET visit_order = visit_order + :delta, version = version + 1
        WHERE travel_plan_id = :planId AND visit_order BETWEEN :from AND :to
        """)
    int shift(UUID planId, int from, int to, int delta);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(nativeQuery = true, value = "UPDATE locations SET visit_order = :order WHERE id = :id")
    int setOrder(UUID id, int order);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(nativeQuery = true, value = "DELETE FROM locations WHERE id = :id")
    int deleteByIdReturningCount(UUID id);
}

