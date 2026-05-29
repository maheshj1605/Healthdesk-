# HealthDesk — Clinic Appointment & EMR System

A full-stack clinic management system built with **Spring Boot + Spring Security + React + MySQL**.

## Features
- Patient registration & appointment booking
- Doctor double-booking prevention
- Electronic Medical Records (EMR)
- Prescription PDF download (patients, doctors, receptionists)
- Role-based access: Doctor / Receptionist / Patient
- JWT authentication with BCrypt password hashing
- Input validation on all API endpoints
- Unit tests for core business logic

## Tech Stack
| Layer      | Technology |
|------------|------------|
| Backend    | Java 17, Spring Boot 3.2, Spring Security |
| Auth       | JWT (jjwt 0.12.3) |
| Database   | MySQL 8, Spring Data JPA, HikariCP |
| PDF        | iText 5.5.13 |
| Frontend   | React 18, React Router, Axios, Tailwind CSS |
| Deploy     | Railway (backend) + Vercel (frontend) + PlanetScale (DB) |

## Project Structure
```
healthdesk/
├── backend/
│   └── src/main/java/com/healthdesk/
│       ├── config/       SecurityConfig, GlobalExceptionHandler
│       ├── controller/   AuthController, AppointmentController, EMRController, UserController
│       ├── dto/          AuthRequest, RegisterRequest, AppointmentRequest, EMRRequest, AuthResponse
│       ├── model/        User, Appointment, EMR, Role, AppointmentStatus
│       ├── repository/   UserRepository, AppointmentRepository, EMRRepository
│       ├── security/     JwtUtil, JwtAuthFilter
│       └── service/      AuthService, AppointmentService, EMRService, PrescriptionPdfService
├── frontend/
│   └── src/
│       ├── components/   Navbar, Card, Button, Alert
│       ├── context/      AuthContext
│       ├── pages/        Login, Register, Dashboard, BookAppointment,
│       │                 MyAppointments, EMRList, CreateEMR, PatientRecords
│       └── services/     api.js (Axios + interceptors)
├── .env.example
├── docker-compose.yml
└── .github/workflows/ci.yml
```

## Local Development

### Prerequisites
- Java 17+, Maven 3.9+, Node.js 20+, MySQL 8

### Backend
```bash
cd backend
mvn spring-boot:run   # runs on http://localhost:8080
```

### Frontend
```bash
cd frontend
npm install
npm run dev           # runs on http://localhost:5173
```

### Docker (full stack)
```bash
cp .env.example .env  # edit JWT_SECRET
docker-compose up --build
# Frontend: http://localhost:80   Backend: http://localhost:8080
```

## API Endpoints

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | /api/auth/register | Public | Register user |
| POST | /api/auth/login | Public | Login, get JWT |
| GET  | /api/doctors | Public | List all doctors |
| POST | /api/appointments | PATIENT | Book appointment |
| GET  | /api/appointments/mine | Any authed | My appointments |
| GET  | /api/appointments/all | RECEPTIONIST | All appointments |
| PUT  | /api/appointments/{id}/status | DR/RECEP | Update status |
| POST | /api/emr | DOCTOR | Create EMR |
| GET  | /api/emr/patient/{id} | DR/RECEP | Patient records |
| GET  | /api/emr/mine | PATIENT | My own records |
| GET  | /api/emr/{id}/pdf | All authed | Download prescription PDF |
| GET  | /api/admin/patients | RECEPTIONIST | List all patients |

## Security Notes
- Set `JWT_SECRET` via environment variable in production — never hardcode it
- Set `DDL_AUTO=validate` in production
- CORS origins are configured via `CORS_ORIGINS` env var
