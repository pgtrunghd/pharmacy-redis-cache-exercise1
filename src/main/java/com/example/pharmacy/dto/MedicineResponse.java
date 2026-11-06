package com.example.pharmacy.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public record MedicineResponse(Long id, String name, BigDecimal price) implements Serializable {}

