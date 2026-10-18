package ua.sumdu.traveler.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ua.sumdu.traveler.entity.TravelPlan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface TravelPlanRepository extends JpaRepository<TravelPlan, UUID> {

    @EntityGraph(attributePaths = "locations")      // один JOIN замість N+1
    Optional<TravelPlan> findWithLocationsById(UUID id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(nativeQuery = true, value = """
        UPDATE travel_plans SET
            title = :title,
            description = CAST(:description AS text),
            start_date = CAST(:startDate AS date),
            end_date = CAST(:endDate AS date),
            budget = CAST(:budget AS numeric),
            currency = :currency,
            is_public = :isPublic,
            version = version + 1
        WHERE id = :id AND version = :version
        """)
    int updateWithVersion(UUID id, int version, String title, String description,
                          LocalDate startDate, LocalDate endDate, BigDecimal budget,
                          String currency, boolean isPublic);

    @Modifying(clearAutomatically = true)
    @Query("delete from TravelPlan p where p.id = :id")
    int deleteByIdReturningCount(UUID id);

    /** Коротке блокування батьківського рядка: серіалізує зміни порядку в межах одного плану. */
    @Query(nativeQuery = true, value = "SELECT id FROM travel_plans WHERE id = :id FOR NO KEY UPDATE")
    Optional<UUID> lockById(UUID id);

    @Query("select p.version from TravelPlan p where p.id = :id")
    Optional<Integer> findVersion(UUID id);
}