# RateMyRental - Complete Setup Guide

**Last Updated**: 2026-07-30  
**Version**: 1.0

---

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Environment Setup](#environment-setup)
3. [Backend Setup](#backend-setup)
4. [Database Setup](#database-setup)
5. [Frontend Setup](#frontend-setup)
6. [Running the Application](#running-the-application)
7. [Verification](#verification)
8. [Troubleshooting](#troubleshooting)

---

## Prerequisites

### System Requirements
- **OS**: Windows 10+, macOS 10.15+, or Linux (Ubuntu 20.04+)
- **RAM**: Minimum 8GB (16GB recommended)
- **Disk Space**: At least 10GB free

### Required Software
1. **Java 21.0.11 LTS**
2. **PostgreSQL 18+**
3. **Maven 3.8+**
4. **Node.js 18+ and npm**
5. **Git**
6. **Docker** (optional, for containerization)

---

## Environment Setup

### 1. Install Java 21

**Windows:**
```bash
# Download from https://www.oracle.com/java/technologies/downloads/
# Run installer and set JAVA_HOME
setx JAVA_HOME "C:\Program Files\Java\jdk-21.0.11"
setx PATH "%JAVA_HOME%\bin;%PATH%"

# Verify
java -version
# Output: java version "21.0.11" LTS
```

**macOS (using Homebrew):**
```bash
brew install java
brew tap homebrew/cask-versions
brew install --cask java21
```

**Linux (Ubuntu):**
```bash
sudo apt-get update
sudo apt-get install openjdk-21-jdk
java -version
```

### 2. Install PostgreSQL 18

**Windows:**
- Download from https://www.postgresql.org/download/windows/
- Run installer
- Note: username and password during installation
- Add PostgreSQL bin to PATH

**macOS:**
```bash
brew install postgresql@18
brew services start postgresql@18
```

**Linux (Ubuntu):**
```bash
sudo apt-get install postgresql-18
sudo systemctl start postgresql
```

### 3. Install Maven

**Windows:**
```bash
# Download from https://maven.apache.org/download.cgi
# Extract to C:\Maven
# Add to PATH: C:\Maven\bin
mvn -version
```

**macOS/Linux:**
```bash
brew install maven  # macOS
# or
sudo apt-get install maven  # Linux
mvn -version
```

### 4. Install Node.js & npm

Download from https://nodejs.org/
```bash
node --version  # v18+
npm --version   # v9+
```

### 5. Install Git

```bash
# Windows: https://git-scm.com/download/win
# macOS: brew install git
# Linux: sudo apt-get install git

git --version
```

---

## Backend Setup

### Step 1: Clone Repository

```bash
git clone https://github.com/shrishailbiradar043-max/ratemyrental.git
cd ratemyrental
```

### Step 2: Verify Project Structure

```
ratemyrental/
├── src/
│   ├── main/
│   │   ├── java/com/ratemyrental/
│   │   │   ├── RateMyRentalApplication.java
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   └── entity/
│   │   └── resources/
│   │       └── application.yml
│   └── test/
├── pom.xml
├── README.md
└── spec/
```

### Step 3: Update application.yml

Edit `src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: ratemyrental
  
  datasource:
    url: jdbc:postgresql://localhost:5432/ratemyrental
    username: shrishailsql
    password: YOUR_PASSWORD_HERE
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: create-drop  # or validate, update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
  
  flyway:
    enabled: false  # Enable after setting up migrations

server:
  port: 8080

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

### Step 4: Build with Maven

```bash
# Clean and build (skip tests initially)
mvn clean package -DskipTests

# Or build with tests
mvn clean package

# Build output
# Output: target/ratemyrental-0.0.1-SNAPSHOT.jar
```

### Step 5: Resolve Dependencies

```bash
# Download all dependencies (can take 2-3 minutes on first run)
mvn dependency:resolve

# Check for issues
mvn clean verify
```

---

## Database Setup

### Step 1: Create Database User

```bash
# Connect to PostgreSQL as default admin
psql -U postgres -h localhost

# Inside psql prompt:
CREATE USER shrishailsql WITH PASSWORD 'Shri@55word';
ALTER ROLE shrishailsql CREATEDB;
\q
```

### Step 2: Create Database

```bash
# Using psql
psql -U postgres -h localhost -c "CREATE DATABASE ratemyrental OWNER shrishailsql;"

# Or connect and create
psql -U shrishailsql -h localhost
CREATE DATABASE ratemyrental;
\q
```

### Step 3: Verify Connection

```bash
# Test connection
psql -U shrishailsql -h localhost -d ratemyrental -c "SELECT version();"

# Should output PostgreSQL version info
```

### Step 4: Initialize Schema (Optional)

If using Flyway migrations:
```bash
# Create migration directory
mkdir -p src/main/resources/db/migration

# Create first migration
cat > src/main/resources/db/migration/V1__Initial_Schema.sql << 'EOF'
-- Will be created by Hibernate for now
EOF
```

---

## Frontend Setup

### Step 1: Create React App Structure

```bash
# Create frontend directory (at ratemyrental root)
npx create-react-app frontend

# Navigate to frontend
cd frontend
```

### Step 2: Install Dependencies

```bash
# Install core dependencies
npm install

# Install additional packages
npm install axios react-router-dom
npm install -D tailwindcss postcss autoprefixer
npm install redux react-redux @reduxjs/toolkit

# Initialize Tailwind
npx tailwindcss init -p
```

### Step 3: Configure Tailwind CSS

Update `frontend/tailwind.config.js`:

```javascript
/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,jsx}",
  ],
  theme: {
    extend: {},
  },
  plugins: [],
}
```

### Step 4: Setup API Client

Create `frontend/src/services/api.js`:

```javascript
import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add JWT token to requests
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export default api;
```

---

## Running the Application

### Option 1: Run Backend Only (Development)

**Terminal 1 - Spring Boot:**
```bash
cd ratemyrental
export JAVA_HOME="C:\Program Files\Java\jdk-21.0.11"  # Windows
mvn spring-boot:run
```

**Output:**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

Started RateMyRentalApplication in 5.626 seconds
```

### Option 2: Run Full Stack (Frontend + Backend)

**Terminal 1 - Backend:**
```bash
cd ratemyrental
mvn spring-boot:run
# Runs on http://localhost:8080
```

**Terminal 2 - Frontend:**
```bash
cd frontend
npm start
# Opens browser on http://localhost:3000
```

### Option 3: Run with Docker

**Build Docker Image:**
```bash
docker build -t ratemyrental:latest .
```

**Run Container:**
```bash
docker run -p 8080:8080 \
  -e DB_URL="jdbc:postgresql://host.docker.internal:5432/ratemyrental" \
  -e DB_USER="shrishailsql" \
  -e DB_PASSWORD="Shri@55word" \
  ratemyrental:latest
```

---

## Verification

### Backend Verification

```bash
# 1. Check application is running
curl http://localhost:8080/api/v1/health

# Expected response:
# {"status":"UP","message":"RateMyRental API is running","timestamp":1785305928664}

# 2. Access Swagger UI
# Browser: http://localhost:8080/swagger-ui.html

# 3. Check API documentation
# Browser: http://localhost:8080/v3/api-docs

# 4. Actuator health
curl http://localhost:8080/actuator/health
```

### Database Verification

```bash
# Connect and verify schema
psql -U shrishailsql -h localhost -d ratemyrental

# List tables
\dt

# Check connections
\conninfo

# Exit
\q
```

### Frontend Verification

```bash
# React app should open automatically
# Check console for any errors
# Verify API connectivity in Network tab (DevTools)
```

---

## Troubleshooting

### Issue: Java 21 not found
**Solution:**
```bash
# Set JAVA_HOME before running
export JAVA_HOME="C:\Program Files\Java\jdk-21.0.11"  # Windows
export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-21.0.11.jdk/Contents/Home"  # macOS

# Verify
java -version
```

### Issue: PostgreSQL connection refused
**Solution:**
```bash
# Check if PostgreSQL is running
# Windows: Services -> PostgreSQL
# macOS: brew services list
# Linux: sudo systemctl status postgresql

# Verify connection
psql -U shrishailsql -h localhost -d ratemyrental

# Check credentials in application.yml
```

### Issue: Port 8080 already in use
**Solution:**
```bash
# Find process using port 8080
# Windows: netstat -ano | findstr :8080
# macOS/Linux: lsof -i :8080

# Kill process or use different port
# Update application.yml: server.port: 8081
```

### Issue: Maven dependency download fails
**Solution:**
```bash
# Clear Maven cache
mvn clean

# Force update
mvn dependency:purge-local-repository

# Try again
mvn clean package
```

### Issue: Frontend cannot connect to backend
**Solution:**
```bash
# Check CORS configuration in WebSecurityConfig.java
# Update API_BASE_URL in frontend/src/services/api.js
# Ensure backend is running on correct port
# Check firewall settings
```

### Issue: React hot reload not working
**Solution:**
```bash
# Delete node_modules
rm -rf frontend/node_modules

# Reinstall
cd frontend
npm install

# Start again
npm start
```

---

## Common Commands

### Backend Commands
```bash
# Build without tests
mvn clean package -DskipTests

# Run tests
mvn test

# Run specific test
mvn test -Dtest=UserControllerTest

# Skip tests and run application
mvn spring-boot:run

# Generate API documentation
mvn javadoc:javadoc
```

### Database Commands
```bash
# Backup database
pg_dump -U shrishailsql -h localhost ratemyrental > backup.sql

# Restore database
psql -U shrishailsql -h localhost -d ratemyrental < backup.sql

# Connect to database
psql -U shrishailsql -h localhost -d ratemyrental
```

### Frontend Commands
```bash
# Start development server
npm start

# Build for production
npm run build

# Run tests
npm test

# Install new package
npm install package-name

# Update packages
npm update
```

### Git Commands
```bash
# Clone repository
git clone https://github.com/shrishailbiradar043-max/ratemyrental.git

# Create feature branch
git checkout -b feature/amazing-feature

# Commit changes
git commit -m "feat(scope): description"

# Push to remote
git push origin feature/amazing-feature

# Create Pull Request on GitHub
```

---

## Next Steps

1. ✅ Setup backend and database
2. ✅ Verify application is running
3. ⬜ Setup frontend React application
4. ⬜ Implement User authentication endpoints
5. ⬜ Create Property CRUD endpoints
6. ⬜ Build Search functionality
7. ⬜ Implement Reviews and Ratings
8. ⬜ Add image upload to AWS S3
9. ⬜ Setup CI/CD pipeline with GitHub Actions
10. ⬜ Deploy to AWS

---

## Support

**Issues?** Open a GitHub issue: https://github.com/shrishailbiradar043-max/ratemyrental/issues

**Questions?** Contact: shrishail@ratemyrental.com

---

**Last Updated**: 2026-07-30  
**Maintained By**: Shrishail  
**Status**: Active Development 🚀
