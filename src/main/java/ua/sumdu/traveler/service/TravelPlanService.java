package ua.sumdu.traveler.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.sumdu.traveler.dto.PlanRequest;
import ua.sumdu.traveler.entity.TravelPlan;
import ua.sumdu.traveler.exception.NotFoundException;
import ua.sumdu.traveler.exception.VersionConflictException;
import ua.sumdu.traveler.repository.TravelPlanRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TravelPlanService {
    private final TravelPlanRepository plans;

    @Transactional
    public TravelPlan create(PlanRequest r) {
        TravelPlan p = new TravelPlan();
        apply(p, r);
        return plans.saveAndFlush(p);          // @Generated підтягне version/created_at
    }

    @Transactional(readOnly = true)
    public TravelPlan get(UUID id) {
        return plans.findWithLocationsById(id)
                .orElseThrow(() -> new NotFoundException("Plan " + id));
    }

    @Transactional
    public TravelPlan update(UUID id, PlanRequest r) {
        if (r.version() == null) throw new IllegalArgumentException("version is required");
        int rows = plans.updateWithVersion(id, r.version(), r.title(), r.description(),
                r.startDate(), r.endDate(), r.budget(),
                r.currency() == null ? "USD" : r.currency(),
                Boolean.TRUE.equals(r.isPublic()));
        if (rows == 0) {                       // 0 rows affected означає або 404, або 409
            Integer cur = plans.findVersion(id)
                    .orElseThrow(() -> new NotFoundException("Travel plan not found"));
            throw new VersionConflictException("Conflict: Travel plan was modified by another user", cur);
        }
        return plans.findById(id).orElseThrow();   // кеш очищено, тож читаємо свіжу версію
    }

    @Transactional
    public void delete(UUID id) {
        if (plans.deleteByIdReturningCount(id) == 0) throw new NotFoundException("Plan " + id);
    }

    private void apply(TravelPlan p, PlanRequest r) {
        p.setTitle(r.title());
        p.setDescription(r.description());
        p.setStartDate(r.startDate());
        p.setEndDate(r.endDate());
        p.setBudget(r.budget());
        if (r.currency() != null) {
            p.setCurrency(r.currency());
        }
        p.setPublicPlan(Boolean.TRUE.equals(r.isPublic()));
    } /* скопіювати поля з r у p */
}

