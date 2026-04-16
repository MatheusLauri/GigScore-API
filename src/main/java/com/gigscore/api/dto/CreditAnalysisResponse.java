package com.gigscore.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreditAnalysisResponse(
        String analysisId,
        String status,
        Integer score,
        Boolean approved,
        Integer limit,
        String reason
) {}
