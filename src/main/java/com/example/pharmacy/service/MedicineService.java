package com.example.pharmacy.service;

import com.example.pharmacy.dto.MedicineRequest;
import com.example.pharmacy.dto.MedicineResponse;
import com.example.pharmacy.entity.Medicine;
import com.example.pharmacy.repository.MedicineRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MedicineService {
    private final MedicineRepository repository;

    public MedicineService(MedicineRepository repository) {
        this.repository = repository;
    }

    @Cacheable(value = "medicines", key = "#id")
    public MedicineResponse getById(Long id) {
        Medicine medicine = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicine not found"));
        return toResponse(medicine);
    }

    public MedicineResponse create(MedicineRequest request) {
        Medicine saved = repository.save(new Medicine(request.name(), request.price()));
        return toResponse(saved);
    }

    private MedicineResponse toResponse(Medicine medicine) {
        return new MedicineResponse(medicine.getId(), medicine.getName(), medicine.getPrice());
    }
}

