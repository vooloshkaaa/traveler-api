package ua.sumdu.traveler.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.sumdu.traveler.dto.LocationRequest;
import ua.sumdu.traveler.dto.LocationResponse;
import ua.sumdu.traveler.dto.PlanRequest;
import ua.sumdu.traveler.dto.PlanResponse;
import ua.sumdu.traveler.repository.TravelPlanRepository;
import ua.sumdu.traveler.service.LocationService;
import ua.sumdu.traveler.service.TravelPlanService;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/travel-plans") @RequiredArgsConstructor
class PlanController {
    private final TravelPlanService plans;
    private final LocationService locations;
    private final TravelPlanRepository repo;

    @GetMapping
    List<PlanResponse> list(@PageableDefault(size = 20) Pageable p) {
        return repo.findAll(p).map(PlanResponse::from).getContent();
    }

    @PostMapping
    ResponseEntity<PlanResponse> create(@Valid @RequestBody PlanRequest r) {
        var saved = plans.create(r);
        return ResponseEntity.created(URI.create("/api/travel-plans/" + saved.getId()))
                .body(PlanResponse.from(saved));          // 201
    }

    @GetMapping("/{id}")
    PlanResponse get(@PathVariable UUID id) { return PlanResponse.withLocations(plans.get(id)); }  // 200

    @PutMapping("/{id}")
    PlanResponse update(@PathVariable UUID id, @Valid @RequestBody PlanRequest r) {
        return PlanResponse.from(plans.update(id, r));                  // 200 / 404 / 409
    }

    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable UUID id) { plans.delete(id); }           // 204

    @PostMapping("/{id}/locations")
    ResponseEntity<LocationResponse> addLocation(@PathVariable UUID id, @Valid @RequestBody LocationRequest r) {
        return ResponseEntity.status(201).body(LocationResponse.from(locations.add(id, r)));
    }
}
