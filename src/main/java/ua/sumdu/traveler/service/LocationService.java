package ua.sumdu.traveler.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.sumdu.traveler.dto.LocationRequest;
import ua.sumdu.traveler.entity.Location;
import ua.sumdu.traveler.exception.NotFoundException;
import ua.sumdu.traveler.repository.LocationRepository;
import ua.sumdu.traveler.repository.TravelPlanRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepository locations;
    private final TravelPlanRepository plans;

    @Transactional
    public Location add(UUID planId, LocationRequest r) {
        validate(r, true);
        plans.lockById(planId).orElseThrow(() -> new NotFoundException("Travel plan not found"));
        Location l = new Location();
        l.setTravelPlan(plans.getReferenceById(planId));
        applyFields(l, r);
        l.setVisitOrder(locations.maxOrder(planId) + 1);   // безпечно: рядок плану заблоковано
        return locations.saveAndFlush(l);
    }

    @Transactional
    public Location update(UUID id, LocationRequest r) {
        validate(r, false);
        Location l = locations.findById(id)
                .orElseThrow(() -> new NotFoundException("Location not found"));
        if (r.name() != null)          l.setName(r.name());
        if (r.address() != null)       l.setAddress(r.address());
        if (r.latitude() != null)      l.setLatitude(r.latitude());
        if (r.longitude() != null)     l.setLongitude(r.longitude());
        if (r.arrivalDate() != null)   l.setArrivalDate(r.arrivalDate());
        if (r.departureDate() != null) l.setDepartureDate(r.departureDate());
        if (r.budget() != null)        l.setBudget(r.budget());
        if (r.notes() != null)         l.setNotes(r.notes());
        return locations.saveAndFlush(l);
    }

    @Transactional
    public void delete(UUID id) {
        if (locations.deleteByIdReturningCount(id) == 0) throw new NotFoundException("Location not found");
        // без перенумерації: прогалини в visit_order дозволені
    }

    private void validate(LocationRequest r, boolean nameRequired) {
        if (r.name() == null ? nameRequired : r.name().isBlank())
            throw new IllegalArgumentException("Name is required");
        if (r.arrivalDate() != null && r.departureDate() != null
                && r.departureDate().isBefore(r.arrivalDate()))
            throw new IllegalArgumentException("Departure must not be before arrival");
    }

    private void applyFields(Location l, LocationRequest r) {
        l.setName(r.name());
        l.setAddress(r.address());
        l.setLatitude(r.latitude());
        l.setLongitude(r.longitude());
        l.setArrivalDate(r.arrivalDate());
        l.setDepartureDate(r.departureDate());
        l.setBudget(r.budget());
        l.setNotes(r.notes());
    }
}
