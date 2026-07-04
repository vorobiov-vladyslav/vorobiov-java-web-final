# University Management System — Фінальний проєкт (Spring Boot + Spring Data JPA + PostgreSQL)

RESTful API системи управління університетом: студенти, викладачі, курси, зарахування,
оцінки, оплати, транскрипти та GPA-звіти.

## Стек

- Java 21, Gradle
- Spring Boot 3.5 (Web, Data JPA, Validation)
- PostgreSQL 16 (docker-compose)
- Flyway (міграції схеми та сід-дані)
- springdoc-openapi (Swagger UI)
- JUnit 5, Mockito, Spring Test, Testcontainers

## Передумови

- JDK 21+
- Docker (Docker Desktop або OrbStack) — для БД та інтеграційних тестів

## Запуск

```bash
docker compose up -d
./gradlew bootRun
```

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI-схема: http://localhost:8080/v3/api-docs

PostgreSQL мапиться на порт **5433** хоста (щоб не конфліктувати з локальним PostgreSQL на 5432).
Міграції Flyway створюють схему та наповнюють БД демо-даними
(5 викладачів, 10 курсів, 20 студентів, 40 зарахувань).

## Тести

```bash
./gradlew test
```

19 тестів: юніт-тести сервісів (Mockito), веб-зріз контролера (`@WebMvcTest`),
репозиторії та наскрізний сценарій (`@DataJpaTest` / `@SpringBootTest` + Testcontainers
з реальним PostgreSQL — потрібен запущений Docker).

## Архітектура

Багатошарова: `controller` → `service` → `repository` → PostgreSQL.
DTO (Java records) на вході/виході, JPA-сутності не виходять за межі сервісного шару.
Централізована обробка помилок через `@RestControllerAdvice`.

```
src/main/java/ua/university/sms/
├── controller/   REST-ендпоінти
├── service/      бізнес-логіка (GPA, зарахування, звіти)
├── repository/   Spring Data JPA
├── mapper/       ручні мапери entity ↔ DTO
├── model/
│   ├── entity/   Student, Teacher, Course, Enrollment (implements Payable)
│   ├── enums/    StudentStatus, TeacherPosition, Grade
│   └── dto/      records запитів/відповідей
├── config/       OpenAPI
└── exception/    винятки та @RestControllerAdvice
```

## Основні ендпоінти

| Метод | Шлях | Опис |
|---|---|---|
| CRUD | `/api/students` | + фільтри `?status=`, `?year=`, пагінація/сортування |
| GET | `/api/students/search?query=` | пошук за частиною ПІБ або email |
| GET | `/api/students/{id}/transcript` | транскрипт із GPA |
| CRUD | `/api/teachers` | 409 при видаленні викладача з курсами |
| CRUD | `/api/courses` | + фільтри `?teacherId=`, `?credits=`; 409 при видаленні курсу із зарахуваннями |
| POST | `/api/enrollments` | зарахування (початково `grade=NA`, `paid=false`); 409 за дубль |
| PUT | `/api/enrollments/{id}/grade` | поставити оцінку |
| PUT | `/api/enrollments/{id}/paid` | позначити оплату (`Payable`) |
| GET | `/api/reports/unpaid-students` | студенти з неоплаченими курсами |
| GET | `/api/reports/course-gpa/{courseId}` | середній GPA курсу |
| GET | `/api/reports/semester-gpa?year=&semester=` | середній GPA семестру |
| GET | `/api/reports/top-students?limit=` | топ-N студентів за GPA |

Повний перелік з усіма параметрами — у Swagger UI.

## GPA

Оцінки мапляться в бали: A=4.0, B=3.0, C=2.0, D=1.0, F=0.0; `NA` (ще не оцінено)
виключається з розрахунку. GPA студента — середньозважений за кредитами курсів:

```
GPA = Σ(бали × кредити) / Σ(кредити)
```

Округлення до 2 знаків (HALF_UP). Якщо оцінених курсів немає — `gpa = null`.

## Формат помилок

```json
{
  "timestamp": "2026-07-04T12:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Student with id 42 not found",
  "path": "/api/students/42",
  "fieldErrors": [{"field": "email", "message": "must be a well-formed email address"}]
}
```

400 — невалідні дані (з `fieldErrors`), 404 — сутність не знайдена, 409 — конфлікт
(дубль email/зарахування, видалення сутності із залежностями).
