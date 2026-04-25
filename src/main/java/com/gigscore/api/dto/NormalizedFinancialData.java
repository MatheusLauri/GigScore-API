package com.gigscore.api.dto;

import java.math.BigDecimal;

public record NormalizedFinancialData(
        String analysisId,
        String cpf,
        BigDecimal totalMonthlyIncome,
        Double platformReputationScore, // Média de avaliações nas plataformas (0 a 5)
        Integer totalCompletedGigs,     // Soma de corridas + entregas
        Integer paymentHistoryScore     // Score do Open Finance (0 a 1000)
) {}
