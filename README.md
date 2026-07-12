# 🎯 AI Interview Preparation System
### Multi-Language Coding Mentor & Interview Practice Platform

A full-stack AI-powered interview preparation system for fresher software developers. Supports **14+ programming languages**, AI-generated questions (MCQ, Coding, Theory, Output, Debug), mock interviews, progress tracking, and personalized study plans — all powered by **Google Gemini AI**.

---

## 📋 Table of Contents
1. [Tech Stack](#tech-stack)
2. [Features](#features)
3. [Project Structure](#project-structure)
4. [Database Setup](#database-setup)
5. [Configuration](#configuration)
6. [How to Run](#how-to-run)
7. [API Reference](#api-reference)
8. [Supported Languages](#supported-languages)
9. [Screenshots Guide](#screenshots-guide)

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 21, Spring Boot 3.5.x |
| AI | Spring AI 1.0.0, Google Gemini (Vertex AI) |
| Database | MySQL 8.x |
| Security | Spring Security 6, JWT, BCrypt |
| ORM | Spring Data JPA, Hibernate |
| Frontend | HTML5, CSS3, Bootstrap 5, Vanilla JS |
| Build | Maven |
| Templates | Thymeleaf 3.x |

---

## ✨ Features

### Student Features
- ✅ Register & Login with JWT authentication
- ✅ Select from 14+ programming languages
- ✅ Choose topic, difficulty (Easy/Medium/Hard), and question type
- ✅ AI-generated MCQs, Coding Problems, Theory Questions
- ✅ AI-generated Output Questions (predict what code prints)
- ✅ AI-generated Debugging Challenges (find & fix bugs)
- ✅ AI evaluates submitted answers automatically
- ✅ Detailed AI feedback: score, grade, strengths, mistakes, tips
- ✅ Mock Interview mode with AI as interviewer
- ✅ Progress tracking per topic & language
- ✅ Weak topic analysis with AI insights
- ✅ Personalized study plan generator
- ✅ AI Mentor chat (ask any coding question)
- ✅ Code review feature
- ✅ Dark/Light mode
- ✅ Fully responsive (mobile, tablet, desktop)

### Admin Features
- ✅ Admin dashboard with platform stats
- ✅ View all registered users
- ✅ Test completion statistics

---

## 📁 Project Structure

```
ai-interview-prep/
├── src/main/
│   ├── java/com/interviewprep/
│   │   ├── AiInterviewPrepApplication.java     # Main entry point
│   │   ├── config/
│   │   │   ├── SecurityConfig.java             # Spring Security + JWT
│   │   │   └── AppConfig.java                  # ModelMapper, ThreadPool
│   │   ├── controller/
│   │   │   ├── PageController.java             # Thymeleaf page routes
│   │   │   ├── AuthController.java             # /api/auth/register, /login
│   │   │   ├── TestController.java             # /api/tests/create, /submit
│   │   │   ├── DashboardController.java        # /api/dashboard
│   │   │   ├── MockInterviewController.java    # /api/mock-interview
│   │   │   ├── StudyPlanController.java        # /api/study-plan
│   │   │   ├── TopicController.java            # /api/topics
│   │   │   ├── AiController.java               # /api/ai/chat, /code-review
│   │   │   └── AdminController.java            # /api/admin
│   │   ├── dto/
│   │   │   ├── request/                        # RegisterRequest, LoginRequest...
│   │   │   └── response/                       # AuthResponse, TestResponse...
│   │   ├── entity/
│   │   │   ├── User.java
│   │   │   ├── Topic.java
│   │   │   ├── Test.java
│   │   │   ├── Question.java
│   │   │   ├── Answer.java
│   │   │   ├── AiFeedback.java
│   │   │   ├── Progress.java
│   │   │   ├── MockInterview.java
│   │   │   └── StudyPlan.java
│   │   ├── exception/
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── ResourceNotFoundException.java
│   │   │   └── ResourceAlreadyExistsException.java
│   │   ├── repository/                         # Spring Data JPA repositories
│   │   ├── security/
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   └── UserDetailsServiceImpl.java
│   │   ├── service/
│   │   │   ├── AiService.java                  # Core Gemini AI integration
│   │   │   ├── AuthService.java
│   │   │   ├── TestService.java
│   │   │   ├── DashboardService.java
│   │   │   ├── MockInterviewService.java
│   │   │   └── StudyPlanService.java
│   │   └── util/
│   │       └── JwtUtil.java
│   └── resources/
│       ├── application.properties
│       ├── static/
│       │   ├── css/main.css                    # Full responsive stylesheet
│       │   └── js/main.js                      # API client, Auth, Toast, Utils
│       └── templates/
│           ├── index.html                      # Landing page
│           ├── login.html
│           ├── register.html
│           ├── dashboard.html
│           ├── topics.html                     # Language & topic selection
│           ├── test.html                       # Test-taking interface
│           ├── results.html                    # Results & AI feedback
│           ├── mock-interview.html
│           ├── progress.html
│           ├── study-plan.html
│           ├── profile.html
│           └── admin.html
├── docs/
│   └── schema.sql                             # Full MySQL schema + seed data
└── pom.xml
```

---

## 🗄️ Database Setup

### Step 1 – Create Database
```sql
CREATE DATABASE ai_interview_prep CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Step 2 – Run Schema
```bash
mysql -u root -p ai_interview_prep < docs/schema.sql
```

### Tables Created
| Table | Purpose |
|-------|---------|
| `users` | Student & admin accounts |
| `topics` | Programming language/topic catalog |
| `tests` | Test sessions |
| `questions` | AI-generated questions per test |
| `answers` | Student answers |
| `ai_feedback` | AI-generated feedback per test |
| `progress` | Per-user per-topic progress tracking |
| `mock_interviews` | Mock interview sessions |
| `study_plans` | AI-generated study plans |

---

## ⚙️ Configuration

Edit `src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/ai_interview_prep
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD

# Google Gemini (Vertex AI)
spring.ai.vertex.ai.gemini.project-id=YOUR_GCP_PROJECT_ID
spring.ai.vertex.ai.gemini.location=us-central1
spring.ai.vertex.ai.gemini.chat.options.model=gemini-1.5-flash

# JWT Secret (must be 256+ bits)
app.jwt.secret=aVeryLongAndSecureSecretKeyForJWTTokenGeneration2024AIInterviewPrep
```

### Google Gemini Setup
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a project & enable **Vertex AI API**
3. Create a service account with Vertex AI User role
4. Download the JSON key file
5. Set environment variable:
   ```bash
   export GOOGLE_APPLICATION_CREDENTIALS=/path/to/key.json
   ```

**Alternative (simpler) – Google AI Studio:**
Use `spring-ai-openai` starter with Gemini-compatible endpoint if using API key.

---

## 🚀 How to Run

### Prerequisites
- Java 21+
- Maven 3.8+
- MySQL 8.x
- Google Cloud account with Vertex AI enabled

### Steps

```bash
# 1. Clone the project
cd ai-interview-prep

# 2. Set up database
mysql -u root -p < docs/schema.sql

# 3. Configure application.properties
# Edit src/main/resources/application.properties

# 4. Set Google credentials
export GOOGLE_APPLICATION_CREDENTIALS=/path/to/your/gcp-key.json

# 5. Build
mvn clean install

# 6. Run
mvn spring-boot:run
# OR
java -jar target/ai-interview-prep-1.0.0.jar
```

### Access
| URL | Page |
|-----|------|
| http://localhost:8080 | Landing Page |
| http://localhost:8080/register | Register |
| http://localhost:8080/login | Login |
| http://localhost:8080/dashboard | Student Dashboard |
| http://localhost:8080/topics | Start Practice |
| http://localhost:8080/mock-interview | Mock Interview |
| http://localhost:8080/progress | Progress Tracking |
| http://localhost:8080/study-plan | Study Plan Generator |
| http://localhost:8080/admin | Admin Dashboard |

### Default Admin Credentials
```
Username: admin
Password: Admin@123
```

---

## 📡 API Reference

### Authentication
```http
POST /api/auth/register
Content-Type: application/json
{
  "fullName": "John Doe",
  "username": "johndoe",
  "email": "john@example.com",
  "password": "Password123",
  "college": "MIT",
  "graduationYear": 2025,
  "targetRole": "Software Engineer"
}

POST /api/auth/login
{
  "usernameOrEmail": "johndoe",
  "password": "Password123"
}
```

### Tests (requires Bearer token)
```http
POST /api/tests/create
Authorization: Bearer <token>
{
  "topicId": 1,
  "language": "Python",
  "difficulty": "MEDIUM",
  "testType": "MCQ",
  "numberOfQuestions": 10
}

POST /api/tests/submit
{
  "testId": 42,
  "answers": { "101": "A", "102": "C", "103": "def solution(): ..." },
  "timeTaken": 720
}

GET /api/tests/{testId}
GET /api/tests/my
```

### Dashboard
```http
GET /api/dashboard
GET /api/dashboard/progress
GET /api/dashboard/weak-topics
```

### Mock Interview
```http
POST /api/mock-interview/start
{
  "topicId": 1,
  "language": "Java",
  "difficulty": "MEDIUM",
  "targetRole": "Software Engineer",
  "companyType": "PRODUCT"
}

POST /api/mock-interview/{id}/respond?answer=Your answer&questionNumber=1
GET /api/mock-interview/my
```

### Study Plan
```http
POST /api/study-plan/generate
{
  "language": "Python",
  "targetRole": "Backend Developer",
  "durationWeeks": 4,
  "experienceLevel": "FRESHER"
}
GET /api/study-plan/active
```

### AI Features
```http
POST /api/ai/chat
{ "message": "Explain recursion in Python", "language": "Python" }

POST /api/ai/code-review
{ "code": "...", "language": "Python", "problem": "Find sum of array" }
```

### Admin
```http
GET /api/admin/stats
Authorization: Bearer <admin-token>
```

---

## 🌐 Supported Languages & Topics

| Language | Topics Covered |
|----------|---------------|
| ☕ Java | Core Java, OOP, Collections, Multithreading, Java 8+, JVM |
| 🐍 Python | Basics, OOP, Data Structures, Libraries, Decorators |
| 🟨 JavaScript | ES6+, DOM, Async, Promises, Node.js |
| 🔷 TypeScript | Types, Interfaces, Generics, Decorators |
| ⚙️ C | Pointers, Memory, Structures, File I/O |
| 🔵 C++ | OOP, STL, Templates, Smart Pointers |
| 💜 C# | OOP, LINQ, Async/Await, .NET |
| 🐹 Go | Goroutines, Channels, Interfaces, Error Handling |
| 🎯 Kotlin | Coroutines, Data Classes, Extension Functions |
| 🐘 PHP | Basics, OOP, Web Concepts |
| 🗄️ SQL | Queries, JOINs, Indexing, Optimization |
| 🧮 DSA | Arrays, Trees, Graphs, DP, Sorting |
| 🍃 Spring Boot | REST, Security, JPA, AI |
| 🏗️ System Design | HLD, LLD, Scalability, API Design |

### Question Types
| Type | Description |
|------|-------------|
| MCQ | 4-option multiple choice |
| CODING | Write a complete solution |
| THEORY | Explain concepts |
| OUTPUT | Predict what code prints |
| DEBUGGING | Find & fix bugs |
| MIXED | Combination of all types |

---

## 🎨 UI Features

- **Light & Dark Mode** – Toggle with 🌙/☀️ button
- **Responsive Sidebar** – Collapses on mobile with hamburger menu
- **Loading Animations** – Pulsing AI orb while Gemini generates
- **Toast Notifications** – Success/error/warning toasts
- **Score Circle** – Animated SVG score visualization
- **Progress Bars** – Animated bar charts by topic
- **Code Editor** – Dark-themed textarea for coding questions
- **Chat Interface** – Bubble-style chat for mock interviews & AI mentor

---

## 🔧 Development Notes

### JPA DDL Mode
The project uses `spring.jpa.hibernate.ddl-auto=validate`.  
Run `schema.sql` first to create tables, then start the app.

For development, you can change to:
```properties
spring.jpa.hibernate.ddl-auto=update
```

### AI Response Parsing
All AI responses are requested as JSON. The `AiService` strips markdown code fences before parsing. If Gemini returns non-JSON, errors are caught gracefully.

### Security
- All `/api/**` except `/api/auth/**` require JWT Bearer token
- Tokens are stored in `localStorage` on the frontend
- BCrypt with strength 12 for passwords

---

## 📞 Support

For issues, check:
1. MySQL connection string in `application.properties`
2. Google Cloud credentials are set correctly
3. Gemini API quota is not exceeded
4. Java 21+ is installed (`java -version`)

---

*Built with ❤️ for fresher developers worldwide*
