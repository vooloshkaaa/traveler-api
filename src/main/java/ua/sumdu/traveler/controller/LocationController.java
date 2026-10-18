package ua.sumdu.traveler.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ua.sumdu.traveler.dto.LocationRequest;
import ua.sumdu.traveler.dto.LocationResponse;
import ua.sumdu.traveler.service.LocationService;

import java.util.UUID;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PutMapping("/{id}")
    LocationResponse update(@PathVariable UUID id, @Valid @RequestBody LocationRequest r) {
        return LocationResponse.from(locationService.update(id, r));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable UUID id) {
        locationService.delete(id);
    }
}