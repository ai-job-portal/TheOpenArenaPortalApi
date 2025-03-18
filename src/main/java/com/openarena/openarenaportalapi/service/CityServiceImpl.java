// src/main/java/com/openarena/openarenaportalapi/service/CityServiceImpl.java
package com.openarena.openarenaportalapi.service;

import com.openarena.openarenaportalapi.dto.CityDTO;
import com.openarena.openarenaportalapi.exceptions.AlreadyExistException;
import com.openarena.openarenaportalapi.exceptions.NotFoundException;
import com.openarena.openarenaportalapi.model.City;
import com.openarena.openarenaportalapi.dto.CityDTO;
import com.openarena.openarenaportalapi.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    @Autowired
    public CityServiceImpl(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Override
    @Transactional
    public CityDTO addCity(CityDTO cityDTO) {
        if (cityRepository.existsByNameAndState(cityDTO.getName(), cityDTO.getState())) {
            throw new AlreadyExistException("City with name '" + cityDTO.getName() + "' and state '" + cityDTO.getState() + "' already exists");
        }

        City city = convertToEntity(cityDTO);
        city = cityRepository.save(city);
        return convertToDTO(city);
    }

    @Override
    @Transactional
    public List<CityDTO> addCities(List<CityDTO> cityDTOs) {
        // Efficient duplicate check using a single query
        List<String> cityStatePairs =
                cityDTOs.stream()
                        .map(dto -> dto.getName().toLowerCase() + "|" + dto.getState().toLowerCase())
                        .collect(Collectors.toList());

        for (CityDTO cityDTO : cityDTOs) {
            if (cityRepository.existsByNameAndState(cityDTO.getName(), cityDTO.getState())) {
                throw new AlreadyExistException(
                        "City with name '"
                                + cityDTO.getName()
                                + "' and state '"
                                + cityDTO.getState()
                                + "' already exists");
            }
        }

        List<City> cities = cityDTOs.stream().map(this::convertToEntity).collect(Collectors.toList());
        cities = cityRepository.saveAll(cities);
        return cities.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CityDTO updateCity(Integer id, CityDTO cityDTO) {
        City city = cityRepository.findById(id).orElseThrow(() -> new NotFoundException("City not found with id: " + id));

        // Prevent duplicates, excluding the current city being updated
        if (!city.getName().equalsIgnoreCase(cityDTO.getName()) || !city.getState().equalsIgnoreCase(cityDTO.getState()))
        {
            if (cityRepository.existsByNameAndState(cityDTO.getName(), cityDTO.getState())) {
                throw new AlreadyExistException("City with name '" + cityDTO.getName() + "' and state '" + cityDTO.getState() + "' already exists");
            }
        }

        city.setName(cityDTO.getName());
        city.setState(cityDTO.getState());
        city = cityRepository.save(city);
        return convertToDTO(city);
    }

    @Override
    @Transactional
    public void deleteCity(Integer id) {
        if (!cityRepository.existsById(id)) {
            throw new NotFoundException("City not found with id: " + id);
        }
        cityRepository.deleteById(id);
    }


    @Override
    @Transactional(readOnly = true)
    public List<CityDTO> getRecentCities() {
        return cityRepository.findTop10ByOrderByCreatedAtDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CityDTO> getAllCities(Pageable pageable) {
        return cityRepository.findAllByOrderByCreatedAtDesc(pageable).map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CityDTO> searchCities(String query, Pageable pageable) {
        return cityRepository.searchByNameOrState(query, pageable).map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CityDTO> searchCities(String query) {
        return cityRepository.searchByNameOrState(query).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public City getCityById(Integer id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("City not found with id: " + id));
    }

    @Override
    public City findByNameAndState(String name, String state) {
        return cityRepository.findByNameAndState(name,state).get();
    }

    private CityDTO convertToDTO(City city) {
        return new CityDTO(city.getId(), city.getName(), city.getState());
    }

    private City convertToEntity(CityDTO cityDTO) {
        City city = new City();
        city.setName(cityDTO.getName());
        city.setState(cityDTO.getState());
        return city;
    }
}