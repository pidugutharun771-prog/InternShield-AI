# 🛡️ InternShield AI

### AI-Inspired Internship Scam Detection & Safety Platform

InternShield AI is a Java full-stack web application designed to help students identify potentially fraudulent or suspicious internship opportunities before sharing sensitive information or making payments.

The platform uses an **explainable rule-based risk analysis engine**, URL safety analysis, company/recruiter verification, PostgreSQL-backed analysis history, analytics, and deployment health monitoring.

---

## 🚀 Live Application

### 🌐 Frontend
**Vercel:**  
https://intern-shield-ai-five.vercel.app/

### ⚙️ Backend API
**Render:**  
https://internshield-ai-yhu1.onrender.com

### ❤️ Backend Health Check
https://internshield-ai-yhu1.onrender.com/actuator/health

### 💻 Source Code
**GitHub:**  
https://github.com/pidugutharun771-prog/InternShield-AI

---

## 🎯 Problem Statement

Fake internship offers are increasingly used to deceive students through:

- Registration and processing fees
- Security deposits
- Guaranteed selection claims
- No-interview selection
- Fake recruiter identities
- Suspicious websites
- Public email addresses
- Requests for sensitive information
- Urgency and pressure tactics
- Suspicious payment methods

Students may lose money or expose personal information before realizing that an opportunity is fraudulent.

**InternShield AI provides an early-warning system that explains suspicious indicators and gives students actionable safety recommendations.**

---

# ✨ Key Features

## 1. 🔍 Internship Risk Analyzer

Users can enter internship offer information and receive an explainable risk assessment.

The analyzer detects indicators such as:

- Registration fees
- Processing fees
- Security deposits
- Payment requests
- No-interview selection
- Guaranteed selection
- Guaranteed placement
- Urgency or limited-seat pressure
- Requests for sensitive information
- Suspicious communication channels
- Cryptocurrency or gift-card requests
- Public/free recruiter email domains
- Suspicious external links

### Results include:

- Risk score
- Risk level
- Detected indicators
- Explanation of detected risks
- Safety recommendations

### Risk Levels

| Score | Risk Level |
|---:|---|
| 0–24 | 🟢 LOW |
| 25–44 | 🟡 MEDIUM |
| 45–69 | 🟠 HIGH |
| 70–100 | 🔴 CRITICAL |

---

## 2. 🔗 URL Safety Analyzer

InternShield AI analyzes internship and job URLs for common security warning signs.

It checks for:

- HTTP instead of HTTPS
- Suspicious keywords
- URL shorteners
- IP-address URLs
- Suspicious ports
- Deep subdomains
- Excessive hyphens
- Executable file extensions
- Suspicious URL structures
- Public recruiter email domains
- Email/website domain mismatch

The system returns a risk score, risk level, detected indicators, and recommendations.

---

## 3. 🏢 Company & Recruiter Verification

The company verification module evaluates information such as:

- Company name
- Company website
- Website domain
- Recruiter email
- Recruiter email domain
- Public email providers
- Email/website domain matching
- Company-name/domain consistency
- Suspicious domain structures

### Verification Results

The system provides:

- Verification score
- Verification status
- Findings
- Recommendations

For example, a company using a public email address such as `gmail.com` while claiming to represent a large organization can receive a verification warning.

---

## 4. 📊 Analytics Dashboard

The analytics dashboard provides an overview of analyzed internship opportunities.

It includes:

- Total scans
- Low-risk scans
- Medium-risk scans
- High-risk scans
- Critical-risk scans
- Risk distribution
- Threat exposure summary

This helps users understand the overall pattern of analyzed opportunities.

---

## 5. 🕘 Persistent Analysis History

Internship analyses are stored in **PostgreSQL**.

Users can retrieve previous analyses through the application.

This demonstrates persistent backend data management using:

**Spring Data JPA + Hibernate + PostgreSQL**

---

## 6. ❤️ System Health Monitoring

The backend uses **Spring Boot Actuator** for application health monitoring.

Health endpoint:

```text
GET /actuator/health
```

This is also used by the Render deployment for service health monitoring.

---

# 🏗️ System Architecture

```text
                    ┌─────────────────────────┐
                    │     React + Vite        │
                    │       Frontend          │
                    │        Vercel            │
                    └────────────┬────────────┘
                                 │
                                 │ REST API
                                 ▼
                    ┌─────────────────────────┐
                    │    Spring Boot API      │
                    │        Backend           │
                    │         Render           │
                    └────────────┬────────────┘
                                 │
                         JPA / Hibernate
                                 │
                                 ▼
                    ┌─────────────────────────┐
                    │       PostgreSQL        │
                    │         Render           │
                    └─────────────────────────┘
```

---

# ☁️ Deployment Architecture

```text
                    GitHub Repository
                           │
              ┌────────────┴────────────┐
              │                         │
              ▼                         ▼
        Vercel                      Render
     React + Vite               Spring Boot API
        Frontend                      │
                                      │
                                      ▼
                              Render PostgreSQL
```

### Deployment Services

| Component | Platform |
|---|---|
| Source Code | GitHub |
| Frontend | Vercel |
| Backend | Render |
| Database | Render PostgreSQL |
| Backend Container | Docker |
| Backend Build | Maven |

---

# 🛠️ Technology Stack

| Layer | Technology |
|---|---|
| Programming Language | Java 21 |
| Backend Framework | Spring Boot 3.5.6 |
| REST API | Spring Web |
| Security | Spring Security |
| Authentication Architecture | JWT-ready |
| Persistence | Spring Data JPA |
| ORM | Hibernate |
| Database | PostgreSQL |
| Frontend | React |
| Frontend Build Tool | Vite |
| Backend Build Tool | Maven |
| Containerization | Docker |
| Monitoring | Spring Boot Actuator |
| Version Control | Git / GitHub |
| Backend Deployment | Render |
| Frontend Deployment | Vercel |

---

# 📁 Project Structure

```text
InternShield-AI/
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
│   │       │
│   │       └── resources/
│   │
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

# ⚙️ Running Locally

## Prerequisites

Install:

- Java 21+
- Maven 3.9+
- Node.js
- npm
- PostgreSQL 16+
- Git

---

## 1. Clone the Repository

```bash
git clone https://github.com/pidugutharun771-prog/InternShield-AI.git
cd InternShield-AI
```

---

## 2. Configure PostgreSQL

Create a PostgreSQL database named:

```text
internshield
```

Configure the backend using environment variables:

```text
DB_URL=jdbc:postgresql://localhost:5432/internshield
DB_USERNAME=postgres
DB_PASSWORD=your_password
JWT_SECRET=your_long_random_secret
```

> Never commit real passwords, database credentials, JWT secrets, or `.env` files to GitHub.

---

## 3. Start the Backend

```bash
cd backend
mvn spring-boot:run
```

The backend normally runs at:

```text
http://localhost:8080
```

If port 8080 is already occupied, configure another available port.

---

## 4. Start the Frontend

Open another terminal:

```bash
cd frontend
npm install
npm run dev
```

The Vite development server normally runs at:

```text
http://localhost:5173
```

---

# 🔌 API Endpoints

## Internship Analysis

```http
POST /api/analysis
```

Analyze an internship opportunity and return risk information.

```http
GET /api/analysis/recent
```

Retrieve recent analysis history.

---

## URL Safety

```http
POST /api/url/analyze
```

Analyze an internship or job URL.

---

## Company Verification

```http
POST /api/company/verify
```

Verify company and recruiter information.

---

## Analytics

```http
GET /api/analytics
```

Retrieve analytics and risk distribution information.

---

## Health

```http
GET /actuator/health
```

Check backend service health.

---

# 🧠 Explainable Risk Scoring

InternShield AI uses a rule-based scoring approach.

Different suspicious indicators contribute different weights to the overall score.

The final score is mapped to:

| Score | Level |
|---:|---|
| 0–24 | LOW |
| 25–44 | MEDIUM |
| 45–69 | HIGH |
| 70–100 | CRITICAL |

Instead of only displaying a score, the system also identifies the **specific indicators that contributed to the risk assessment**.

This makes the result easier for students to understand.

---

# 🧪 Functional Testing

The application was tested using multiple scenarios.

### Test 1 — Suspicious Internship

Detected indicators included:

- Registration fee
- No interview
- Public recruiter email

**Result:** HIGH risk

---

### Test 2 — Legitimate Internship

No major suspicious indicators detected.

**Result:** LOW risk

---

### Test 3 — Extreme Scam

Detected indicators included:

- Security deposit
- Guaranteed selection
- Sensitive financial/authentication information
- Public email

**Result:** CRITICAL risk

---

### Test 4 — Suspicious URL

Example:

```text
http://secure-login-paypal.example.com/verify-account
```

Detected:

- HTTP instead of HTTPS
- Suspicious URL keywords

**Result:** MEDIUM risk

---

### Test 5 — Safe URL

Example:

```text
https://www.google.com
```

No major suspicious indicators detected.

**Result:** LOW risk

---

### Test 6 — Suspicious Company Verification

A company claiming to represent a major organization but using an unrelated public recruiter email.

**Result:** NEEDS VERIFICATION

---

### Test 7 — Consistent Company Verification

Company website and recruiter email domain matched.

**Result:** VERIFIED

---

# 🔐 Security

The application includes a Spring Security foundation with:

- Stateless API configuration
- CORS configuration
- CSRF configuration
- API endpoint authorization rules
- Restricted actuator exposure
- JWT-ready security architecture

The current academic demonstration version does not require a login flow.

---

# 🎓 Project Objectives

The main objectives of InternShield AI are to:

- Help students identify suspicious internship offers.
- Detect common internship scam indicators.
- Explain why an offer may be risky.
- Identify suspicious URLs.
- Verify company and recruiter information.
- Maintain analysis history.
- Provide analytics about detected risks.
- Demonstrate Java full-stack development.
- Demonstrate REST API development.
- Demonstrate database integration.
- Demonstrate cloud deployment and containerization.

---

# 🔮 Future Enhancements

Possible future improvements include:

- AI/ML-based scam classification
- PDF internship offer analysis
- Image and OCR analysis
- Browser extension
- Advanced company verification
- Real-time domain reputation checking
- Email analysis
- External cybersecurity intelligence
- Redis caching
- Rate limiting
- Admin dashboard
- Feedback-based model improvement
- Automated threat intelligence integration

---

# ⚠️ Disclaimer

InternShield AI provides automated risk indicators and safety recommendations based on information submitted by the user.

The results **do not guarantee that an internship is genuine or fraudulent**.

Users should independently verify companies, recruiters, websites, and internship offers through official channels before:

- Making payments
- Sharing identity documents
- Sharing banking information
- Sharing passwords or authentication codes
- Accepting an internship offer

---

# 👩‍💻 Project Information

**Project:** InternShield AI

**Type:** Java Full-Stack Academic Project

**Focus:** Internship Scam Detection & Student Safety

**Stack:**

```text
Java
Spring Boot
React
Vite
PostgreSQL
Docker
GitHub
Render
Vercel
```

---

## 🌟 Live Project

👉 **Try InternShield AI:**  
https://intern-shield-ai-five.vercel.app/

🛡️ **InternShield AI — helping students think twice before trusting a suspicious internship offer.**