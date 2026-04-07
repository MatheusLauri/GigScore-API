package com.gigscore.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record SolicitacaoAnaliseCredito(
        @NotBlank
        @Size(min = 11, max = 11)
        @Pattern(regexp = "\\d{11}", message = "CPF deve conter exatamente 11 dígitos numéricos")
        String cpf,

        @NotNull
        @DecimalMin(value = "0.01", message = "Renda mensal deve ser maior que zero")
        @Digits(integer = 12, fraction = 2)
        BigDecimal rendaMensal,

        @PositiveOrZero
        Integer mesesNaGigEconomia
) {
}
