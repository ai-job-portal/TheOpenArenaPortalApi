// src/main/java/com/openarena/openarenaportalapi/service/CityService.java
package com.openarena.openarenaportalapi.service;

import com.openarena.openarenaportalapi.model.City;
import com.openarena.openarenaportalapi.dto.CityDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CityService {
    CityDTO addCity(CityDTO cityDTO);
    List<CityDTO> addCities(List<CityDTO> cityDTOs);
    CityDTO updateCity(Integer id, CityDTO cityDTO);
    void deleteCity(Integer id);
    List<CityDTO> getRecentCities();
    Page<CityDTO> getAllCities(Pageable pageable);
    Page<CityDTO> searchCities(String query, Pageable pageable);
    List<CityDTO> searchCities(String query);
    City getCityById(Integer id); // Add this
    City findByNameAndState(String name, String state);
}