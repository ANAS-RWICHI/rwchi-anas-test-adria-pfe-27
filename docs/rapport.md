# Rapport – Épreuve technique ADRIA ENSET PFE 2027

Nom / Prénom : Rwichi Anas
Repository : https://github.com/<compte>/rwichi-anas-test-adria-pfe-27

## Partie A – Préparation
- Repository privé créé (capture)
- Projet Maven multi-modules : un module par micro-service (capture de l'arborescence + `mvnw package`)

## Partie B – Conception et architecture
- Diagramme de classes
- Schéma d'architecture

## Partie C – Micro-services fonctionnels
- C1 account-service : REST / GraphQL / MCP (captures)
- C2 transaction-service : REST / GraphQL / MCP, règles métier (captures)
- C3 OpenFeign + Resilience4j (capture du fallback, account-service arrêté)

## Partie D – Micro-services techniques
- Gateway, Eureka (capture du dashboard), Config (`bank.transaction.max-amount=1000`)

## Partie E – Chatbot agentique
- Captures client REST / Telegram

## Partie F – Kafka
- Capture de l'événement produit et du log du consumer

## Partie G – Client web

## Partie H – Détection de fraude
- Résultats + réponses aux 4 questions

## Partie I – DevOps
- Dockerfile, docker-compose, pipeline CI
- Secrets vs configuration ; probes ; logs/métriques/traces ; rollback
