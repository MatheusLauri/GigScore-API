package com.gigscore.api.service;

import com.gigscore.api.dto.CreditAnalysisResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mock de um Banco de Dados em memória para simular a persistência.
 * No futuro, será substituído por um Repository do Spring Data JPA.
 */
@Service
public class DatabaseService {

    private static final ConcurrentHashMap<String, CreditAnalysisResponse> databaseMock = new ConcurrentHashMap<>();

    public void save(CreditAnalysisResponse response) {
        databaseMock.put(response.analysisId(), response);
    }

    public CreditAnalysisResponse findById(String analysisId) {
        return databaseMock.get(analysisId);
    }

    public List<CreditAnalysisResponse> findAll() {
        return new ArrayList<>(databaseMock.values());
    }
}
