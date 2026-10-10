# InternShield AI 🛡️

**InternShield AI** is a Java full-stack internship safety platform designed to help students identify potentially fraudulent or suspicious internship opportunities before sharing information or making payments.

The system combines explainable rule-based risk analysis, URL safety checks, company/recruiter verification, persistent scan history, and an analytics dashboard.

## 🚀 Live Demo

**Frontend:**  
https://internshield-frontend-ygy9.onrender.com

**Backend API:**  
https://internshield-ai-0pzs.onrender.com

**Health Check:**  
https://internshield-ai-0pzs.onrender.com/actuator/health

## 📂 Source Code

GitHub:  
https://github.com/Chikatla-Manaswini/InternShield-AI.git

---

## ✨ Features

### 1. Internship Risk Analyzer
Analyzes internship offer text and identifies suspicious signals such as:

- Registration or processing fees
- Payment/deposit requests
- No-interview selection
- Guaranteed jobs or placements
- Urgency and limited-seat pressure
- Suspicious communication channels
- Cryptocurrency/gift-card requests
- Requests for sensitive information
- Suspicious external links
- Free/public recruiter email domains

The system produces:

- Risk score
- Risk level
- Detected indicators
- Safety recommendations

Risk levels:

`LOW` → `MEDIUM` → `HIGH` → `CRITICAL`

### 2. URL Safety Analyzer
Checks internship/job URLs for common warning signs, including:

- URL shorteners
- Suspicious keywords
- IP-address URLs
- HTTP instead of HTTPS
- Suspicious ports
- Deep subdomains
- Excessive hyphens
- Executable file extensions
- Suspicious URL patterns
- Public recruiter email domains
- Email/website domain mismatch

### 3. Company Verification
Checks:

- Company website
- Website domain
- Recruiter email domain
- Public email providers
- Email/website domain matching
- Company-name/domain consistency
- Suspicious domain structure

Returns a verification score, status, findings, and recommendations.

### 4. Analytics Dashboard
Provides an overview of analyzed internship offers:

- Total scans
- Low-risk scans
- Medium-risk scans
- High-risk scans
- Critical-risk scans
- Risk distribution
- Threat exposure summary

### 5. Persistent Analysis History
Internship risk analyses are stored in PostgreSQL so previous analyses can be retrieved through the backend.

### 6. API Health Monitoring
Spring Boot Actuator provides a health endpoint for deployment monitoring.

---

## 🏗️ Architecture

```text
                    ┌──────────────────────────┐
                    │       React + Vite       │
                    │        Frontend          │
                    └────────────┬─────────────┘
                                 │ REST API
                                 ▼
                    ┌──────────────────────────┐
                    │    Spring Boot Backend   │
                    │                          │
                    │  Risk Analysis           │
                    │  URL Safety              │
                    │  Company Verification    │
                    │  Analytics               │
                    │  Security Configuration  │
                    └────────────┬─────────────┘
                                 │ JPA / Hibernate
                                 ▼
                    ┌──────────────────────────┐
                    │       PostgreSQL         │
                    │        Database          │
                    └──────────────────────────┘
```

### Deployment Architecture

```text
GitHub
  │
  ├── Render Static Site
  │       └── React + Vite Frontend
  │
  └── Render Web Service
          └── Spring Boot Backend
                    │
                    ▼
             Render PostgreSQL
```

---

## 🛠️ Technology Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Backend | Spring Boot 3.5.6 |
| REST API | Spring Web |
| Validation | Spring Validation |
| Security | Spring Security |
| Persistence | Spring Data JPA / Hibernate |
| Database | PostgreSQL 16 |
| Frontend | React |
| Build Tool | Vite |
| Backend Build | Maven |
| Monitoring | Spring Boot Actuator |
| Containerization | Docker |
| Deployment | Render |
| Version Control | Git / GitHub |

---

## 📁 Project Structure

```text
InternShieldAI/
│
├── backend/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/internshield/
│   │       │       ├── config/
│   │       │       ├── controller/
│   │       │       ├── dto/
│   │       │       ├── model/
│   │       │       ├── repository/
│   │       │       └── service/
│   │       ├── resources/
│   │       └── ...
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   ├── public/
│   ├── package.json
│   └── vite.config.js
│
├── docker-compose.yml
├── .env.example
├── .gitignore
└── README.md
```

---

## ⚙️ Running the Project Locally

### Prerequisites

Install:

- Java 21+
- Maven 3.9+
- Node.js
- npm
- PostgreSQL 16+
- Git

### 1. Clone the repository

```bash
git clone https://github.com/Chikatla-Manaswini/InternShield-AI.git
cd InternShield-AI
```

### 2. Configure PostgreSQL

Create a database named:

```text
internshield
```

Configure the backend database credentials using environment variables.

Example:

```env
DB_URL=jdbc:postgresql://localhost:5432/internshield
DB_USERNAME=postgres
DB_PASSWORD=postgres
JWT_SECRET=replace-with-a-long-random-secret
```

**Do not commit real passwords, secrets, or `.env` files to GitHub.**

### 3. Start the backend

```bash
cd backend
mvn spring-boot:run
```

The backend can be accessed at:

```text
http://localhost:8080
```

If port 8080 is already in use, run Spring Boot on another available port.

### 4. Start the frontend

Open another terminal:

```bash
cd frontend
npm install
npm run dev
```

The Vite development server normally starts at:

```text
http://localhost:5173
```

---

## 🔌 Main API Endpoints

### Internship Analysis

```http
POST /api/analysis
GET  /api/analysis/recent
```

### URL Analysis

```http
POST /api/url/analyze
```

### Company Verification

```http
POST /api/company/verify
```

### Analytics

```http
GET /api/analytics
```

### Health

```http
GET /actuator/health
```

---

## 🔐 Security

The application includes a Spring Security foundation with:

- Stateless API configuration
- CSRF configuration
- CORS configuration
- Restricted actuator endpoints
- API endpoint authorization rules
- JWT-ready security architecture

The current college-demo version does not require a login flow.

---

## 🧠 Risk Scoring

The Internship Risk Analyzer uses an explainable rule-based scoring engine.

Different warning signals contribute different amounts to the overall risk score.

The final score is mapped to:

| Score | Risk Level |
|---:|---|
| 0–24 | LOW |
| 25–44 | MEDIUM |
| 45–69 | HIGH |
| 70–100 | CRITICAL |

The system also returns the specific indicators that influenced the result, making the decision easier for students to understand.

> InternShield AI is a decision-support tool and should not be treated as a definitive legal, cybersecurity, or employment verification service.

---

## 🧪 Example

### Suspicious Internship

Input:

```text
Congratulations! You have been selected for an internship.
Pay a registration fee to confirm your position.
No interview required. Guaranteed placement.
Contact us immediately on WhatsApp.
```

The system can identify multiple warning indicators and return a high or critical risk assessment with recommendations.

---

## 🌐 Deployment

The application is deployed using Render.

```text
React Frontend
      ↓
Render Static Site
      ↓
Spring Boot REST API
      ↓
Render PostgreSQL
```

### Production URLs

Frontend:

https://internshield-frontend-ygy9.onrender.com

Backend:

https://internshield-ai-0pzs.onrender.com

Health:

https://internshield-ai-0pzs.onrender.com/actuator/health

---

## 🎯 Project Objectives

- Help students recognize suspicious internship offers.
- Explain why an offer may be risky instead of only displaying a score.
- Detect common financial, communication, domain, and urgency-based scam indicators.
- Provide URL and recruiter/company verification support.
- Maintain analysis data for dashboard insights.
- Provide a scalable Java full-stack foundation for future AI/ML integration.

---

## 🔮 Future Enhancements

Possible future improvements include:

- AI/ML-based scam classification
- PDF/image internship offer analysis
- OCR-based document processing
- Browser extension
- More advanced company verification
- Real-time domain reputation checks
- Email analysis
- Redis caching and rate limiting
- Feedback-based model improvement
- Admin dashboard
- Advanced cybersecurity intelligence

---

## 👩‍💻 Project

**InternShield AI**

A Java full-stack academic project focused on internship scam awareness and safety.

Built with:

**Java + Spring Boot + React + PostgreSQL + Docker + Render**

---

## ⚠️ Disclaimer

InternShield AI provides automated risk indicators and safety recommendations based on the information submitted by the user. Results are not guaranteed to determine whether an internship is genuinely fraudulent. Users should independently verify organizations, recruiters, websites, and offers before making payments or sharing sensitive information.
