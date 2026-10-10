# InternShield AI — Advanced Java Full-Stack Edition

This is a Java/Spring Boot rewrite and expansion of the original Flask InternShield prototype.

## Stack
- Backend: Java 21, Spring Boot, REST API, Spring Data JPA, PostgreSQL, Spring Security, Actuator
- Frontend: React + Vite
- Database: PostgreSQL
- Architecture: REST API + service layer + repository + responsive SPA

## Features
- Internship offer risk analysis
- Weighted scam-signal engine
- Free/public email detection
- Risk levels: LOW / MEDIUM / HIGH / CRITICAL
- Explainable indicators and recommendations
- Persistent analysis history in PostgreSQL
- Recent-analysis dashboard
- Health endpoint via Spring Actuator
- Docker PostgreSQL setup

## Run backend
1. Install Java 21 and Maven.
2. Start database: `docker compose up -d postgres`
3. `cd backend`
4. `mvn spring-boot:run`

API: http://localhost:8080

## Run frontend
1. Install Node.js 20+.
2. `cd frontend`
3. `npm install`
4. `npm run dev`

Frontend: http://localhost:5173

## API
POST `/api/analysis`
```json
{"content":"Pay a registration fee to join this internship. Contact us on Telegram.","email":"hr@gmail.com"}
```
GET `/api/analysis/recent`
GET `/actuator/health`

## Next-level upgrades
- JWT login/registration
- Admin dashboard and moderation
- ML model service / ONNX inference
- Company verification API
- URL reputation checking
- Email/offer PDF upload and OCR
- Chrome extension
- Notifications and audit logs
- Redis caching and rate limiting
- Cloud deployment with managed PostgreSQL
