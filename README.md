# 🚀 BillBharat Sales App - Spring Boot Backend

A production-ready Spring Boot backend application for the BillBharat Sales App, providing comprehensive REST APIs for sales operations, team management, and analytics.

## 📋 Overview

This backend serves a React Native mobile application and provides:
- **53+ REST API endpoints** for complete sales operations
- **JWT-based authentication** with Spring Security 6
- **PostgreSQL database** running on port 5433
- **Real-time GPS tracking** for field executives
- **Commission calculation** and performance analytics
- **Team lead management** with approvals and beat planning
- **Gamification system** with badges and achievements

## 🏗️ Tech Stack

- **Java 17+**
- **Spring Boot 3.2+**
- **Spring Security 6** (JWT Authentication)
- **Spring Data JPA** with Hibernate
- **PostgreSQL 15+** (Docker container)
- **Liquibase** (Database migrations)
- **Lombok** (Reduce boilerplate)
- **MapStruct** (Entity-DTO mapping)
- **Springdoc OpenAPI** (Swagger documentation)
- **Maven** (Dependency management)

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- Docker & Docker Compose
- Git

### 1. Clone the Repository

```bash
git clone https://github.com/indo45517/indostack-sales-backend.git
cd indostack-sales-backend
```

### 2. Start PostgreSQL Database

```bash
docker-compose up -d
```

This will start PostgreSQL on port **5433** (not the default 5432).

### 3. Configure Environment Variables

Create a `.env` file or set environment variables:

```properties
# Database
DB_HOST=localhost
DB_PORT=5433
DB_NAME=billbharat_sales
DB_USER=billbharat_user
DB_PASSWORD=billbharat_secure_pass_2026

# JWT Configuration
JWT_SECRET=your-256-bit-secret-key-change-in-production
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000
```

### 4. Build the Application

```bash
mvn clean install
```

### 5. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 6. Access Swagger Documentation

Open your browser and navigate to:
```
http://localhost:8080/swagger-ui.html
```

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api/v1/sales
```

### Authentication
All protected endpoints require a JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

### API Categories

#### 🔐 Authentication (5 endpoints)
- `POST /auth/login` - Login with phone/password
- `POST /auth/register` - Register new user
- `POST /auth/verify-otp` - Verify OTP (optional)
- `POST /auth/refresh` - Refresh JWT token
- `POST /auth/logout` - Logout user

#### 📍 Attendance (3 endpoints)
- `POST /attendance/check-in` - Check-in with GPS
- `POST /attendance/check-out` - Check-out with GPS
- `GET /attendance` - Get attendance records (paginated)

#### 🏪 Visits (3 endpoints)
- `POST /visits` - Log shop visit
- `GET /visits` - Get visits (paginated)
- `GET /visits/{id}` - Get visit details

#### 💰 Sales (4 endpoints)
- `POST /sales` - Create sale
- `GET /sales` - Get sales (paginated)
- `GET /sales/{id}` - Get sale details
- `GET /coupons/validate` - Validate coupon code

#### 📦 Deliveries (3 endpoints)
- `GET /deliveries` - Get deliveries (paginated)
- `POST /deliveries/{id}/deliver` - Mark delivered
- `POST /inventory/paper-request` - Request paper stock

#### 📊 Stats (4 endpoints)
- `GET /stats/daily` - Daily performance stats
- `GET /stats/monthly` - Monthly performance stats
- `GET /leaderboard` - Sales leaderboard
- `GET /earnings` - Commission and earnings

#### 👥 Team Lead (14 endpoints)
- `GET /teamlead/members/locations` - Real-time GPS tracking
- `GET /team/members` - Get team members
- `GET /team/members/{id}` - Get member details
- `GET /team/analytics` - Team performance
- `GET /approvals` - Pending approvals
- `POST /approvals/{id}/approve` - Process approval
- And more...

#### 🗺️ Territories & Tasks (10 endpoints)
- `GET /territories` - Get all territories
- `POST /territories` - Create territory
- `GET /tasks` - Get tasks
- `POST /tasks` - Create task
- And more...

#### 🏆 Gamification (5 endpoints)
- `GET /achievements` - Get user achievements
- `GET /badges` - Get all badges
- `POST /achievements/unlock` - Unlock achievement
- `GET /achievements/stats` - Gamification stats
- `GET /leaderboard/badges` - Badge leaderboard

#### 🔔 Notifications (5 endpoints)
- `POST /notifications/register` - Register device
- `GET /notifications` - Get notifications
- `POST /notifications/{id}/read` - Mark as read
- `POST /notifications/read-all` - Mark all as read
- `DELETE /notifications/{id}` - Delete notification

#### 📈 Analytics (6 endpoints)
- `GET /analytics/trends` - Sales trends
- `GET /analytics/funnel` - Conversion funnel
- `GET /analytics/revenue` - Revenue breakdown
- `GET /analytics/territory` - Territory analytics
- `GET /analytics/predictions` - Predictive analytics
- `GET /analytics` - Advanced analytics

#### 🗺️ Map (3 endpoints)
- `GET /merchants/map` - Get merchant markers
- `GET /visits/nearby` - Nearby visits
- `POST /route/optimize` - Route optimization

#### 📤 Upload (1 endpoint)
- `POST /upload/image` - Upload image (multipart/form-data)

## 🗄️ Database Schema

The application uses PostgreSQL with the following main tables:

- **users** - User accounts (executives, team leads, owners)
- **attendance_records** - Check-in/check-out with GPS
- **visit_logs** - Shop visit records
- **sales** - Sales transactions
- **coupons** - Discount coupons
- **paper_roll_deliveries** - Delivery tracking
- **territories** - Territory management
- **tasks** - Task assignments
- **badges** - Achievement badges
- **user_achievements** - User progress tracking
- **notifications** - Push notifications
- **merchants** - Merchant/shop details
- **inventory_items** - Inventory tracking
- **commissions** - Commission records

Database migrations are managed by Liquibase.

## 🔒 Security

### JWT Authentication
- Access tokens expire in 24 hours
- Refresh tokens expire in 7 days
- Passwords are hashed using BCrypt
- Role-based access control (OWNER, SALES_LEAD, SALES_EXECUTIVE)

### CORS Configuration
CORS is configured to allow requests from:
- React Native mobile app
- Web admin panel (if applicable)

⚠️ **Important**: Update CORS settings in production!

## 🐳 Docker Support

### PostgreSQL Container

The `docker-compose.yml` file sets up:
- PostgreSQL 15 (Alpine)
- Port: **5433** (different from default 5432)
- Database: `billbharat_sales`
- User: `billbharat_user`
- Password: `billbharat_secure_pass_2026`

```bash
# Start containers
docker-compose up -d

# Stop containers
docker-compose down

# View logs
docker-compose logs -f postgres-billbharat

# Access PostgreSQL CLI
docker exec -it billbharat-db psql -U billbharat_user -d billbharat_sales
```

## 📁 Project Structure

```
src/main/java/com/billbharat/sales/
├── BillBharatSalesApplication.java
├── config/
│   ├── SecurityConfig.java
│   ├── JwtConfig.java
│   ├── CorsConfig.java
│   └── OpenApiConfig.java
├── controller/
│   ├── AuthController.java
│   ├── AttendanceController.java
│   ├── VisitController.java
│   ├── SalesController.java
│   ├── DeliveryController.java
│   ├── StatsController.java
│   ├── TeamLeadController.java
│   ├── TerritoryController.java
│   ├── TaskController.java
│   ├── GamificationController.java
│   ├── NotificationController.java
│   ├── AnalyticsController.java
│   ├── MapController.java
│   └── UploadController.java
├── dto/
│   ├── request/
│   └── response/
├── entity/
│   ├── User.java
│   ├── AttendanceRecord.java
│   ├── VisitLog.java
│   ├── Sale.java
│   └── ... (more entities)
├── repository/
├── service/
│   ├── impl/
├── security/
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   └── UserDetailsServiceImpl.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── BadRequestException.java
└── util/
    ├── ResponseUtil.java
    └── DateUtil.java
```

## 🧪 Testing

Run unit tests:
```bash
mvn test
```

Run integration tests:
```bash
mvn verify
```

## 📦 Building for Production

### Create executable JAR
```bash
mvn clean package -DskipTests
```

The JAR file will be created at `target/billbharat-sales-backend-1.0.0.jar`

### Run the JAR
```bash
java -jar target/billbharat-sales-backend-1.0.0.jar
```

## 🚀 Deployment

### Environment Variables for Production

```properties
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/billbharat_sales
SPRING_DATASOURCE_USERNAME=your_db_user
SPRING_DATASOURCE_PASSWORD=your_secure_password

# JWT
JWT_SECRET=your-production-secret-key-256-bits
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# Server
SERVER_PORT=8080
```

### Docker Deployment

```bash
# Build Docker image
docker build -t billbharat-backend:latest .

# Run container
docker run -d \
  --name billbharat-backend \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/billbharat_sales \
  -e JWT_SECRET=your-secret-key \
  billbharat-backend:latest
```

## 🔧 Configuration

### Application Properties

Key configuration properties in `application.yml`:

```yaml
spring:
  application:
    name: billbharat-sales-backend
  datasource:
    url: jdbc:postgresql://localhost:5433/billbharat_sales
    username: billbharat_user
    password: billbharat_secure_pass_2026
  jpa:
    hibernate:
      ddl-auto: validate
  liquibase:
    enabled: true

jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000

server:
  port: 8080
```

## 📊 Response Format

All API responses follow this format:

### Success Response
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... }
}
```

### Error Response
```json
{
  "success": false,
  "message": "Error description",
  "error": "Detailed error message"
}
```

### Paginated Response
```json
{
  "success": true,
  "data": [ ... ],
  "page": 1,
  "limit": 20,
  "total": 100,
  "totalPages": 5
}
```

## 🐛 Troubleshooting

### Port 5433 already in use
```bash
# Check what's using the port
lsof -i :5433

# Stop the PostgreSQL container
docker-compose down
```

### Database connection refused
- Ensure PostgreSQL container is running: `docker ps`
- Check database credentials in `application.yml`
- Verify port mapping: `docker port billbharat-db`

### JWT token expired
- Login again to get a new token
- Use the refresh token endpoint

## 📝 License

This project is proprietary and confidential.

## 👥 Team

- **Backend Developer**: Senior Backend Team
- **Frontend Team**: React Native Developers
- **Product Owner**: BillBharat Team

## 📞 Support

For issues or questions, please contact:
- Email: support@billbharat.com
- GitHub Issues: [Create an issue](https://github.com/indo45517/indostack-sales-backend/issues)

---

**Status**: 🚧 In Development  
**Version**: 1.0.0  
**Last Updated**: April 2026
