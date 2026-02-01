Lumen Mall - Backend (Spring Boot)

The robust engine powering Lumen Mall. A Spring Boot application utilizing Spring Security with JWT for stateless authentication and PostgreSQL for data persistence.
🚀 Key Features

    JWT Authentication: Secure login/register flow with role-based access control (USER/ADMIN).

    Order Management: Automated stock deduction and status tracking (AWAITING_PAYMENT, PAID, SHIPPED).

    Email Service: Low-stock alerts and registration verification via Resend API.

    Relational Database: Complex mappings for Users, Products, Orders, and OrderItems.

    CORS Configuration: Fully configured for secure communication with the Render-hosted frontend.

🛠 Tech Stack

    Language: Java 17+

    Framework: Spring Boot 3.x

    Security: Spring Security & JWT (io.jsonwebtoken)

    Database: PostgreSQL

    ORM: Spring Data JPA / Hibernate

⚙️ Setup & Deployment

    Environment Variables: The application expects the following variables (set these in Render or your local application.properties):

        STRIPE_SECRET_KEY: Your Stripe secret key.

        RESEND_API_KEY: API key for email services.

        DATABASE_URL: PostgreSQL connection string.

    Database Migration: The project uses spring.jpa.hibernate.ddl-auto=update. Tables are automatically generated on the first run.

    Build and Run:
    Bash

    ./mvnw clean install
    ./mvnw spring-boot:run

📍 API Endpoints (Quick Reference)
Method	Endpoint	Access
POST	/api/users/login	Public
GET	/api/products	Public
POST	/api/orders	Authenticated
GET	/api/orders/all	Admin Only
PUT	/api/users/profile/update	Authenticated
