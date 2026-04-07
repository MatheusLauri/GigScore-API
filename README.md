# GigScore — Motor de Scoring com Dados Alternativos

## 📌 Contexto do Projeto

O **GigScore** é um motor de scoring de crédito projetado para avaliar o risco de crédito de pessoas sem histórico bancário tradicional (como freelancers, motoristas de aplicativo e trabalhadores da *gig economy*), utilizando fontes de dados alternativos.

Este é um projeto pessoal focado em arquitetura de backend de alta qualidade, servindo tanto como um robusto portfólio de engenharia de software quanto como base para um possível produto real (fintech).

## 📌 O Problema

Modelos tradicionais de análise de crédito dependem fortemente de:
- Comprovação de renda formal (Holerite)
- Score de bureaus tradicionais (ex: Serasa, Boa Vista)

Essa abordagem exclui uma parcela significativa da população que possui renda real, porém não formalizada nos moldes tradicionais. 

**A Solução Proposta:**
Um sistema capaz de:
1. Coletar dados de múltiplas fontes alternativas.
2. Normalizar e padronizar essas informações.
3. Calcular um score de crédito alternativo e justo.
4. Tomar decisões de crédito embasadas e explicáveis.

## 📌 Arquitetura (Alto Nível)

O sistema foi desenhado utilizando uma **Arquitetura Orientada a Eventos (EDA)** para garantir escalabilidade, resiliência e baixo acoplamento entre os componentes.

### Fluxo Principal de Processamento:
1. Cliente solicita análise de crédito através da API.
2. A API publica o evento inicial de solicitação.
3. Dados são coletados via integração com APIs externas.
4. Os dados brutos passam por um pipeline de normalização.
5. O Score de crédito é calculado baseado em regras de negócio.
6. Uma IA gera uma explicação em linguagem natural do resultado obtido.
7. O resultado final e o histórico são persistidos no banco de dados.

## 📌 Tecnologias Principais

- **Linguagem / Framework:** Java 21, Spring Boot 3.4.x
- **Integração:** Apache Camel
- **Mensageria:** Apache Kafka
- **Banco de Dados Relacional:** PostgreSQL (Armazenamento principal)
- **Banco de Dados NoSQL (Opcional):** MongoDB (Para armazenamento de dados brutos/payloads)
- **Inteligência Artificial:** Integração com API de LLM externa (para explicação do score)
- **Infraestrutura:** Docker & Docker Compose

## 📌 Pipeline de Eventos (Kafka)

O fluxo de dados é guiado por tópicos/eventos no Kafka:

`API` → `[credit-analysis-requested]` → `Data Collector` → `[financial-data-collected]` → `Normalizer` → `[normalized-data-ready]` → `Scorer` → `[score-calculated]` → `AI Explainer` → `Database`

## 📌 Componentes e Microsserviços

1. **API / Credit Service:** Ponto de entrada (REST), validação inicial e orquestração da requisição.
2. **Data Collector Service (Apache Camel):** Responsável por integrar com APIs externas e coletar os dados brutos (inicialmente utilizando *mocks*).
3. **Normalizer Service:** Transforma os dados brutos de diferentes provedores em um modelo canônico padronizado.
4. **Scorer Service:** Aplica o motor de regras (Rule Engine) para calcular o score de crédito final.
5. **AI Explainer Service:** Consome o resultado numérico e utiliza uma LLM para gerar uma justificativa/explicação textual compreensível para o usuário final.

## 📌 Escopo e Evolução (Roadmap)

A premissa do desenvolvimento é a **evolução incremental**, partindo de um MVP (Produto Mínimo Viável) pragmático para evitar *overengineering*:

- **Fase 1 (MVP):** 
  - Monolito modular ou microsserviços simplificados.
  - APIs externas de dados simuladas por *mocks*.
  - Motor de regras *hardcoded* ou simples (if/else ou engine básica).
  - IA focada apenas na explicabilidade do score gerado pelas regras (não atua na decisão).
- **Fase 2:** 
  - Integração real com provedores de Open Finance / Gig Platforms.
  - Refinamento da arquitetura orientada a eventos.
  - Implementação de CQRS (se necessário).
- **Fase 3:** 
  - Modelos de Machine Learning treinados para substituir o motor de regras estático.

## 📌 Princípios de Engenharia Adotados

- **Pragmatismo:** Soluções simples primeiro, complexidade apenas quando justificada.
- **Qualidade de Produção:** Código limpo, testes automatizados (Testcontainers, JUnit, Mockito), CI/CD.
- **Observabilidade:** Logs estruturados, tracing distribuído e métricas essenciais desde o dia 1.
- **Segurança:** Validação de dados rigorosa e proteção de PII (Personally Identifiable Information).