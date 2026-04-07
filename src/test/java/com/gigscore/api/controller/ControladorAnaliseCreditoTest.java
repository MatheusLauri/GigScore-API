package com.gigscore.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigscore.api.dto.SolicitacaoAnaliseCredito;
import com.gigscore.api.service.ServicoPontuacaoCredito;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ControladorAnaliseCredito.class)
@Import(ServicoPontuacaoCredito.class)
class ControladorAnaliseCreditoTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void aprovadoQuandoPontuacaoAcimaDoLimite() throws Exception {
        var corpo = new SolicitacaoAnaliseCredito("12345678909", new BigDecimal("5000"), 15);
        mockMvc.perform(post("/analise-credito")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(corpo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pontuacao", is(740)))
                .andExpect(jsonPath("$.aprovado", is(true)));
    }

    @Test
    void reprovadoQuandoAbaixoDoLimite() throws Exception {
        var corpo = new SolicitacaoAnaliseCredito("12345678909", new BigDecimal("1500"), 1);
        mockMvc.perform(post("/analise-credito")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(corpo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pontuacao", is(320)))
                .andExpect(jsonPath("$.aprovado", is(false)));
    }

    @Test
    void retorna400QuandoCpfInvalido() throws Exception {
        var corpo = new SolicitacaoAnaliseCredito("123", new BigDecimal("3000"), null);
        mockMvc.perform(post("/analise-credito")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(corpo)))
                .andExpect(status().isBadRequest());
    }
}
