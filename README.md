# RateMyRental 🏠

A community-driven platform empowering tenants to make informed rental decisions through honest reviews, ratings, and insights about properties, buildings, and landlords.

**Vision**: Become the "Google Reviews" for rental properties.

---

## 🚀 Quick Start

### Prerequisites
- **Java 21.0.11** LTS
- **PostgreSQL 18+**
- **Maven 3.8+**
- **Node.js 18+** (for frontend)

### Backend Setup
```bash
# Clone repository
git clone https://github.com/shrishailbiradar043-max/ratemyrental.git
cd ratemyrental

# Build with Maven
mvn clean package -DskipTests

# Run Spring Boot application
mvn spring-boot:run
```

**Backend Access**: http://localhost:8080
**Swagger UI**: http://localhost:8080/swagger-ui.html
**API Docs**: http://localhost:8080/v3/api-docs

### Database Setup
```bash
# Connect to PostgreSQL
psql -U postgres -h localhost

# Create database (if not exists)
CREATE DATABASE ratemyrental OWNER shrishailsql;
```

---

## 📋 Tech Stack

### Backend
- **Java 21** - Modern LTS version with performance improvements
- **Spring Boot 3.2.0** - Rapid application development framework
- **Spring Security 6.1** - Authentication & authorization
- **Spring Data JPA** - Data persistence layer
- **Hibernate 6.3** - ORM framework
- **PostgreSQL 18** - Enterprise-grade relational database
- **JWT** - Stateless authentication tokens
- **Flyway** - Database migration management
- **Swagger/OpenAPI** - API documentation

### Frontend
- **React 18+** - UI component library
- **Tailwind CSS** - Utility-first CSS framework
- **Redux/Context API** - State management

### DevOps & Cloud
- **Docker** - Containerization
- **AWS S3** - Image/file storage
- **GitHub** - Version control & CI/CD
- **Maven** - Build automation

---

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                        Frontend (React)                      │
│                    Tailwind CSS Styling                      │
└────────────────────────┬────────────────────────────────────┘
                         │ REST API (JSON)
                         │ JWT Authentication
┌────────────────────────▼────────────────────────────────────┐
│                 Spring Boot Backend (Java 21)               │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │         REST Controllers (Spring MVC)                │  │
│  │  - AuthController    - PropertyController           │  │
│  │  - ReviewController  - FavoriteController           │  │
│  └──────────────────────┬───────────────────────────────┘  │
│                         │                                   │
│  ┌──────────────────────▼───────────────────────────────┐  │
│  │    Business Logic Layer (Services)                   │  │
│  │  - UserService      - PropertyService               │  │
│  │  - ReviewService    - RatingService                 │  │
│  └──────────────────────┬───────────────────────────────┘  │
│                         │                                   │
│  ┌──────────────────────▼───────────────────────────────┐  │
│  │    Data Access Layer (Spring Data JPA)              │  │
│  │  - UserRepository   - PropertyRepository            │  │
│  │  - ReviewRepository - RatingRepository              │  │
│  └──────────────────────┬───────────────────────────────┘  │
└────────────────────────┬────────────────────────────────────┘
                         │
         ┌───────────────┼────────────────┐
         │               │                │
    ┌────▼────┐   ┌─────▼──────┐   ┌────▼─────┐
    │PostgreSQL│   │  AWS S3    │   │  Flyway  │
    │Database  │   │  Storage   │   │Migrations│
    └──────────┘   └────────────┘   └──────────┘
```

---

## 🗄️ Core Features

### 1. Property Search
- Search by property name, building, area, city, pincode
- Filters: rent, BHK, rating, parking, lift, security, pet-friendly, furnished, water

### 2. Property Detail Page
- Photos, address, average rating, review count
- Amenities, rent, deposit, landlord rating, map location
- Sections: Overview, Reviews, Amenities, Comparison, Nearby

### 3. Ratings & Reviews
- Sub-ratings: Security, Water, Cleanliness, Maintenance, Parking, Internet, Noise, Lift, Power, Neighborhood, Value
- Review: Title, Description, Pros, Cons, Stay Duration, Move-in Year, Anonymous Option, Images

### 4. Compare Properties
- Compare up to 4 properties
- Compare: Rent, Deposit, Ratings, Amenities, Security, Parking, Water, Reviews

### 5. Favorites
- Bookmark properties
- View saved list
- Quick access to favorite properties

---

## 🔐 Authentication & Security

- **JWT Tokens** - Stateless authentication
- **Spring Security** - Authorization rules
- **Password Encryption** - bcrypt hashing
- **HTTPS/TLS** - Secure communication
- **CORS** - Cross-origin resource sharing configured

---

## 📊 Database Schema

### Core Entities
- **User** - Tenant/user accounts
- **Property** - Rental properties
- **Review** - User reviews for properties
- **Rating** - Sub-ratings for properties
- **Landlord** - Landlord information
- **Favorite** - User bookmarked properties
- **ReviewImage** - Images attached to reviews
- **PropertyImage** - Property photos
- **ReviewLike** - Review upvoting
- **Report** - Reported reviews
- **Amenity** - Property amenities
- **City** - City reference data

---

## 🚢 Deployment

### Docker
```bash
# Build Docker image
docker build -t ratemyrental:latest .

# Run container
docker run -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://db:5432/ratemyrental \
  -e DB_USER=shrishailsql \
  -e DB_PASSWORD=*** \
  ratemyrental:latest
```

### AWS Deployment
- **EC2** - Application servers
- **RDS** - Managed PostgreSQL
- **S3** - Image/file storage
- **CloudFront** - CDN for static assets
- **ELB** - Load balancing

---

## 📈 Scalability

### Horizontal Scaling
- Stateless Spring Boot services
- Load balancer distribution
- Database connection pooling

### Performance
- Search response: <500ms
- Redis caching (future)
- Database indexing on frequently queried columns
- CDN for static files via CloudFront

### Monitoring
- Application metrics via Actuator
- CloudWatch logging
- Performance monitoring

---

## 🔄 API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Refresh JWT token

### Properties
- `GET /api/properties` - Search properties
- `GET /api/properties/{id}` - Get property details
- `POST /api/properties` - Create property (admin)
- `PUT /api/properties/{id}` - Update property
- `DELETE /api/properties/{id}` - Delete property

### Reviews
- `GET /api/properties/{id}/reviews` - Get property reviews
- `POST /api/properties/{id}/reviews` - Add review
- `PUT /api/reviews/{id}` - Edit review
- `DELETE /api/reviews/{id}` - Delete review

### Favorites
- `POST /api/favorites` - Add favorite
- `DELETE /api/favorites/{id}` - Remove favorite
- `GET /api/favorites` - Get user's favorites

### Health
- `GET /api/v1/health` - Application health check
- `GET /actuator/health` - Detailed health status

---

## 📝 Development Workflow

### Branching Strategy
- `main` - Production-ready code
- `develop` - Development branch
- `feature/*` - Feature branches
- `hotfix/*` - Hotfix branches

### Commit Convention
```
<type>(<scope>): <subject>

<body>

<footer>
```

Types: `feat`, `fix`, `chore`, `docs`, `test`, `refactor`

---

## 🤝 Contributing

1. Fork the repository
2. Create feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'feat(core): add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open Pull Request

---

## 📚 Documentation

See [spec/](./spec/) directory for:
- [architecture.md](./spec/architecture.md) - System architecture
- [engineering.md](./spec/engineering.md) - Engineering guidelines
- [setup.md](./spec/setup.md) - Development setup guide
- [api-endpoints.md](./spec/api-endpoints.md) - Complete API reference

---

## 📞 Contact & Support

**Project Lead**: Shrishail
**Repository**: https://github.com/shrishailbiradar043-max/ratemyrental
**Issues**: https://github.com/shrishailbiradar043-max/ratemyrental/issues

---

## 📄 License

MIT License - See LICENSE file for details

---

## 🎯 Roadmap

### Phase 1 (MVP) ✅
- [x] User registration & login
- [x] JWT authentication
- [x] Property search
- [x] Property details
- [x] Reviews & ratings
- [x] Favorites

### Phase 2 (Upcoming)
- [ ] AI review summaries
- [ ] Review sentiment analysis
- [ ] Landlord verification
- [ ] Image moderation

### Phase 3 (Future)
- [ ] Mobile apps (iOS/Android)
- [ ] Maps integration
- [ ] Nearby schools/hospitals
- [ ] Rental price trends
- [ ] AI recommendations

### Phase 4 (Long-term)
- [ ] Property claim system
- [ ] Owner dashboard
- [ ] Premium subscription
- [ ] Verified properties badge

---

**Last Updated**: 2026-07-30  
**Status**: Active Development 🚀
