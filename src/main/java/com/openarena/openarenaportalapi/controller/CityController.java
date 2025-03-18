// src/main/java/com/openarena/openarenaportalapi/controller/CityController.java
package com.openarena.openarenaportalapi.controller;

import com.openarena.openarenaportalapi.dto.CityDTO;
import com.openarena.openarenaportalapi.service.CityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cities")
@PreAuthorize("hasRole('ROLE_JARVIS')") // Secure endpoints
public class CityController {

    private final CityService cityService;

    @Autowired
    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @PostMapping
    public ResponseEntity<CityDTO> addCity(@Valid @RequestBody CityDTO cityDTO) {
        CityDTO savedCity = cityService.addCity(cityDTO);
        return new ResponseEntity<>(savedCity, HttpStatus.CREATED);
    }

    @PostMapping("/bulk") // Corrected path
    public ResponseEntity<List<CityDTO>> addCities(@Valid @RequestBody List<CityDTO> cityDTOs) {
        List<CityDTO> savedCities = cityService.addCities(cityDTOs);
        return new ResponseEntity<>(savedCities, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CityDTO> updateCity(@PathVariable Integer id, @Valid @RequestBody CityDTO cityDTO) {
        CityDTO updatedCity = cityService.updateCity(id, cityDTO);
        return ResponseEntity.ok(updatedCity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Integer id) {
        cityService.deleteCity(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recent") // New endpoint for recent cities
    public ResponseEntity<List<CityDTO>> getRecentCities() {
        List<CityDTO> recentCities = cityService.getRecentCities();
        return ResponseEntity.ok(recentCities);
    }
    @GetMapping
    public ResponseEntity<Page<CityDTO>> getAllCities(Pageable pageable) {
        Page<CityDTO> cityPage = cityService.getAllCities(pageable);
        return ResponseEntity.ok(cityPage);
    }

    // Search endpoint
    @GetMapping("/search")
    public ResponseEntity<Page<CityDTO>> searchCities(@RequestParam("query") String query, Pageable pageable) {
        Page<CityDTO> cities = cityService.searchCities(query, pageable);
        return ResponseEntity.ok(cities);
    }

    //For typeahead control
    @GetMapping("/typeahead")
    public ResponseEntity<List<CityDTO>> searchCity(@RequestParam(name = "q") String query){
        return new ResponseEntity<>(cityService.searchCities(query), HttpStatus.OK);
    }
}