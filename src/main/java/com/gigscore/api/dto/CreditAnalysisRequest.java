package com.gigscore.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CreditAnalysisRequest(
        @NotBlank String userId,
        @NotBlank String cpf
) {}
