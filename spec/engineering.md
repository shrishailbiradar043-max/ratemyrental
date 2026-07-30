# RateMyRental — Engineering Manual

**Version:** 1.0
**Last Updated:** 2026-07-30
**Audience:** Backend, Frontend, and DevOps engineers building/maintaining RateMyRental

---

## 1. Local Setup

### 1.1 Prerequisites

Install the following before starting:

| Tool | Minimum Version | Purpose |
|---|---|---|
| Java (OpenJDK) | 21 | Backend runtime |
| Maven | 3.9+ | Backend build tool |
| Node.js | 20 LTS | Frontend runtime |
| npm | 10+ | Frontend package manager |
| Docker & Docker Compose | 24+ / v2 | Local Postgres/Redis + containerized runs |
| Git | 2.40+ | Version control |
| AWS CLI | 2.x | S3 access (local dev uses either LocalStack or a dev S3 bucket) |

### 1.2 Clone the Repository

```bash
git clone https://github.com/your-org/ratemyrental.git
cd ratemyrental
```

### 1.3 Repository Layout at a Glance

```bash
ratemyrental/
├── backend/     # Spring Boot API
├── frontend/    # React + Tailwind app
├── docker-compose.yml
└── README.md
```

### 1.4 Start Local Infrastructure (Postgres + Redis)

From the repo root:

```bash
docker compose up -d postgres redis
```

`docker-compose.yml` (reference — already included in repo root):

```yaml
version: "3.9"
services:
  postgres:
    image: postgres:16
    container_name: rmr-postgres
    environment:
      POSTGRES_DB: ratemyrental
      POSTGRES_USER: rmr_user
      POSTGRES_PASSWORD: rmr_password
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data

  redis:
    image: redis:7
    container_name: rmr-redis
    ports:
      - "6379:6379"

volumes:
  pgdata:
```

### 1.5 Backend Environment Variables

Create `backend/.env` (never commit this file — it's in `.gitignore`):

```bash
# Database
DB_URL=jdbc:postgresql://localhost:5432/ratemyrental
DB_USERNAME=rmr_user
DB_PASSWORD=rmr_password

# JWT
JWT_SECRET=replace-with-a-long-random-base64-secret
JWT_EXPIRATION_MS=3600000
JWT_REFRESH_EXPIRATION_MS=604800000

# AWS S3
AWS_ACCESS_KEY_ID=your-local-or-dev-access-key
AWS_SECRET_ACCESS_KEY=your-local-or-dev-secret-key
AWS_REGION=ap-south-1
S3_BUCKET_NAME=ratemyrental-dev-media

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# App
SPRING_PROFILES_ACTIVE=local
SERVER_PORT=8080
```

Export the variables into your shell session (or use an `.env` loader plugin / IDE run config):

```bash
export $(grep -v '^#' backend/.env | xargs)
```

### 1.6 Run Database Migrations & Start the Backend

```bash
cd backend
mvn clean install
mvn flyway:migrate
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`.
Swagger UI: `http://localhost:8080/swagger-ui.html`

### 1.7 Frontend Environment Variables

Create `frontend/.env.local`:

```bash
VITE_API_BASE_URL=http://localhost:8080/api/v1
VITE_S3_PUBLIC_BASE_URL=https://ratemyrental-dev-media.s3.ap-south-1.amazonaws.com
```

### 1.8 Install & Run the Frontend

```bash
cd frontend
npm install
npm run dev
```

The app will be available at `http://localhost:5173`.

### 1.9 Running Everything via Docker (Alternative)

To run the full stack (API + frontend + Postgres + Redis) in containers:

```bash
docker compose up --build
```

### 1.10 Running Tests

```bash
# Backend unit + integration tests
cd backend
mvn test

# Frontend unit tests
cd frontend
npm run test

# Frontend e2e tests (if Playwright/Cypress configured)
npm run test:e2e
```

---

## 2. Folder Structure

```text
ratemyrental/
├── .github/
│   └── workflows/
│       ├── backend-ci.yml
│       ├── frontend-ci.yml
│       └── deploy.yml
│
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/ratemyrental/
│   │   │   │   ├── RateMyRentalApplication.java
│   │   │   │   ├── config/                # Spring config: Security, CORS, Swagger, Redis
│   │   │   │   │   ├── SecurityConfig.java
│   │   │   │   │   ├── OpenApiConfig.java
│   │   │   │   │   └── RedisConfig.java
│   │   │   │   ├── controller/             # REST controllers
│   │   │   │   │   ├── AuthController.java
│   │   │   │   │   ├── PropertyController.java
│   │   │   │   │   ├── ReviewController.java
│   │   │   │   │   └── ComparisonController.java
│   │   │   │   ├── dto/                    # Request/response payloads
│   │   │   │   │   ├── request/
│   │   │   │   │   └── response/
│   │   │   │   ├── entity/                 # JPA entities
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── Property.java
│   │   │   │   │   ├── Address.java
│   │   │   │   │   ├── Review.java
│   │   │   │   │   └── Amenity.java
│   │   │   │   ├── repository/             # Spring Data JPA repositories
│   │   │   │   │   ├── UserRepository.java
│   │   │   │   │   ├── PropertyRepository.java
│   │   │   │   │   └── ReviewRepository.java
│   │   │   │   ├── service/                # Business logic
│   │   │   │   │   ├── AuthService.java
│   │   │   │   │   ├── PropertySearchService.java
│   │   │   │   │   ├── ReviewService.java
│   │   │   │   │   ├── ComparisonService.java
│   │   │   │   │   └── S3StorageService.java
│   │   │   │   ├── security/                # JWT filters, providers
│   │   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   │   └── JwtAuthFilter.java
│   │   │   │   ├── exception/                # Global exception handling
│   │   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   │   └── ApiError.java
│   │   │   │   └── util/
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       ├── application-local.yml
│   │   │       ├── application-prod.yml
│   │   │       └── db/migration/            # Flyway SQL migrations
│   │   │           ├── V1__init_schema.sql
│   │   │           ├── V2__add_reviews.sql
│   │   │           └── V3__add_comparisons.sql
│   │   └── test/
│   │       └── java/com/ratemyrental/
│   │           ├── controller/
│   │           ├── service/
│   │           └── repository/
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/
│   ├── public/
│   ├── src/
│   │   ├── main.tsx
│   │   ├── App.tsx
│   │   ├── api/                     # Axios instances + API call functions
│   │   │   ├── client.ts
│   │   │   ├── propertyApi.ts
│   │   │   └── reviewApi.ts
│   │   ├── components/              # Reusable UI components
│   │   │   ├── ui/                  # Buttons, Inputs, Modals (design system)
│   │   │   ├── property/
│   │   │   │   ├── PropertyCard.tsx
│   │   │   │   └── PropertyGallery.tsx
│   │   │   └── review/
│   │   │       └── ReviewForm.tsx
│   │   ├── pages/                   # Route-level pages
│   │   │   ├── SearchPage.tsx
│   │   │   ├── PropertyDetailPage.tsx
│   │   │   ├── ComparePage.tsx
│   │   │   └── LoginPage.tsx
│   │   ├── store/                   # State management (Redux Toolkit / Zustand)
│   │   │   ├── index.ts
│   │   │   ├── slices/
│   │   │   │   ├── authSlice.ts
│   │   │   │   ├── searchSlice.ts
│   │   │   │   └── comparisonSlice.ts
│   │   │   └── queries/              # React Query hooks
│   │   │       ├── usePropertyQuery.ts
│   │   │       └── useReviewMutation.ts
│   │   ├── hooks/
│   │   ├── types/                    # Shared TS interfaces/DTOs
│   │   ├── utils/
│   │   └── styles/
│   │       └── tailwind.css
│   ├── .env.local
│   ├── tailwind.config.js
│   ├── vite.config.ts
│   └── package.json
│
├── docker-compose.yml
├── .gitignore
└── README.md
```

---

## 3. API Contracts

All endpoints are prefixed with `/api/v1`. Authenticated endpoints require an `Authorization: Bearer <jwt>` header. All responses use `application/json`.

### 3.1 `GET /api/v1/properties` — Search Properties

Searches and filters property listings with pagination.

**Request:**

```
GET /api/v1/properties?city=Bengaluru&minRent=15000&maxRent=40000&bedrooms=2&amenities=parking,gym&page=0&size=10&sort=avgRating,desc
```

| Query Param | Type | Required | Description |
|---|---|---|---|
| `city` | string | No | Filter by city |
| `minRent` / `maxRent` | number | No | Rent range filter |
| `bedrooms` | int | No | Exact bedroom count |
| `amenities` | string (CSV) | No | Comma-separated amenity names |
| `page` / `size` | int | No | Pagination (0-indexed) |
| `sort` | string | No | Field + direction (e.g., `avgRating,desc`) |

**Response `200 OK`:**

```json
{
  "content": [
    {
      "id": "3f2a9c1e-4b8d-4e2a-9f3e-1234567890ab",
      "title": "Sunrise 2BHK near MG Road",
      "monthlyRent": 32000,
      "bedrooms": 2,
      "bathrooms": 2,
      "squareFootage": 950.0,
      "propertyType": "APARTMENT",
      "avgRating": 4.3,
      "reviewCount": 27,
      "primaryImageUrl": "https://ratemyrental-media.s3.ap-south-1.amazonaws.com/properties/3f2a9c1e/primary.jpg",
      "address": {
        "city": "Bengaluru",
        "state": "Karnataka",
        "postalCode": "560001",
        "latitude": 12.9716,
        "longitude": 77.5946
      }
    }
  ],
  "page": {
    "number": 0,
    "size": 10,
    "totalElements": 42,
    "totalPages": 5
  }
}
```

**Error Response `400 Bad Request`:**

```json
{
  "timestamp": "2026-07-30T10:15:30Z",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "minRent must be less than or equal to maxRent",
  "path": "/api/v1/properties"
}
```

---

### 3.2 `POST /api/v1/properties/{propertyId}/reviews` — Submit a Review

Creates a new review/rating for a property. Requires authentication.

**Request:**

```
POST /api/v1/properties/3f2a9c1e-4b8d-4e2a-9f3e-1234567890ab/reviews
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
```

```json
{
  "rating": 5,
  "reviewText": "Great location, responsive landlord, and quiet neighborhood. Water pressure could be better.",
  "imageKeys": [
    "reviews/tmp/8b1c2e-photo1.jpg",
    "reviews/tmp/8b1c2e-photo2.jpg"
  ]
}
```

| Field | Type | Required | Constraints |
|---|---|---|---|
| `rating` | integer | Yes | 1–5 |
| `reviewText` | string | Yes | 20–2000 characters |
| `imageKeys` | array\<string\> | No | Max 5 S3 object keys from pre-signed upload step |

**Response `201 Created`:**

```json
{
  "id": "9d8e7f6a-1234-4b5c-8d9e-0987654321fe",
  "propertyId": "3f2a9c1e-4b8d-4e2a-9f3e-1234567890ab",
  "userId": "a1b2c3d4-5678-4e9f-a012-b3c4d5e6f789",
  "rating": 5,
  "reviewText": "Great location, responsive landlord, and quiet neighborhood. Water pressure could be better.",
  "status": "APPROVED",
  "imageUrls": [
    "https://ratemyrental-media.s3.ap-south-1.amazonaws.com/reviews/9d8e7f6a/photo1.jpg",
    "https://ratemyrental-media.s3.ap-south-1.amazonaws.com/reviews/9d8e7f6a/photo2.jpg"
  ],
  "createdAt": "2026-07-30T10:20:11Z",
  "property": {
    "id": "3f2a9c1e-4b8d-4e2a-9f3e-1234567890ab",
    "avgRating": 4.35,
    "reviewCount": 28
  }
}
```

**Response `202 Accepted`** (flagged for moderation):

```json
{
  "id": "9d8e7f6a-1234-4b5c-8d9e-0987654321fe",
  "status": "PENDING_REVIEW",
  "message": "Your review has been submitted and is pending moderation."
}
```

**Error Response `401 Unauthorized`:**

```json
{
  "timestamp": "2026-07-30T10:20:11Z",
  "status": 401,
  "error": "UNAUTHORIZED",
  "message": "JWT token is missing, expired, or invalid",
  "path": "/api/v1/properties/3f2a9c1e-4b8d-4e2a-9f3e-1234567890ab/reviews"
}
```

---

### 3.3 `POST /api/v1/comparisons` — Create a Property Comparison

**Request:**

```json
{
  "name": "Bengaluru shortlist",
  "propertyIds": [
    "3f2a9c1e-4b8d-4e2a-9f3e-1234567890ab",
    "b7c6d5e4-2222-4a1b-9c8d-334455667788"
  ]
}
```

**Response `201 Created`:**

```json
{
  "id": "c1a2b3c4-9999-4d5e-8f7a-112233445566",
  "name": "Bengaluru shortlist",
  "properties": [
    {
      "id": "3f2a9c1e-4b8d-4e2a-9f3e-1234567890ab",
      "title": "Sunrise 2BHK near MG Road",
      "monthlyRent": 32000,
      "avgRating": 4.35,
      "bedrooms": 2,
      "bathrooms": 2
    },
    {
      "id": "b7c6d5e4-2222-4a1b-9c8d-334455667788",
      "title": "Palm Residency 2BHK",
      "monthlyRent": 29500,
      "avgRating": 4.1,
      "bedrooms": 2,
      "bathrooms": 2
    }
  ],
  "createdAt": "2026-07-30T10:25:00Z"
}
```

---

## 4. State Management (Frontend)

The frontend uses a **split state model** — separating *server state* from *client/UI state* — to avoid the common anti-pattern of shoving API data into a global store.

### 4.1 Server State — React Query (TanStack Query)

All data fetched from the backend (property search results, property details, reviews, comparisons) is managed by **React Query**, not Redux. This gives:

- Automatic caching, background refetching, and stale-time control
- Built-in loading/error states per query
- Automatic cache invalidation on mutations (e.g., invalidate `["property", id]` after a review is submitted)

Example hook (`store/queries/usePropertyQuery.ts`):

```ts
export function usePropertyDetail(propertyId: string) {
  return useQuery({
    queryKey: ["property", propertyId],
    queryFn: () => propertyApi.getById(propertyId),
    staleTime: 60_000, // 1 minute
  });
}
```

Example mutation with cache invalidation (`store/queries/useReviewMutation.ts`):

```ts
export function useSubmitReview(propertyId: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: SubmitReviewRequest) =>
      reviewApi.submit(propertyId, payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["property", propertyId] });
      queryClient.invalidateQueries({ queryKey: ["reviews", propertyId] });
    },
  });
}
```

### 4.2 Client/UI State — Redux Toolkit (or Zustand)

State that is **not** derived from the server lives in a lightweight global store:

| Slice | Responsibility |
|---|---|
| `authSlice` | Current user, JWT access token (in memory), auth status |
| `searchSlice` | Active search filters (city, rent range, bedrooms, amenities) — kept in sync with URL query params |
| `comparisonSlice` | Locally selected properties for comparison before they're persisted via `POST /comparisons` |

Example (`store/slices/searchSlice.ts`):

```ts
const searchSlice = createSlice({
  name: "search",
  initialState: {
    city: "",
    minRent: null,
    maxRent: null,
    bedrooms: null,
    amenities: [] as string[],
  },
  reducers: {
    setFilters: (state, action: PayloadAction<Partial<SearchFilters>>) => {
      Object.assign(state, action.payload);
    },
    resetFilters: () => initialSearchState,
  },
});
```

### 4.3 Auth Token Handling

- The **access token** is kept in memory (Redux state), never in `localStorage`, to reduce XSS token-theft risk.
- The **refresh token** is stored in an `HttpOnly`, `Secure` cookie set by the backend, inaccessible to JS.
- An Axios response interceptor (`api/client.ts`) automatically attempts a silent refresh on `401` responses before retrying the original request once.

### 4.4 Form State

Feature forms (Review submission, Property listing creation) use **React Hook Form** with **Zod** schema validation, decoupled from both React Query and Redux — form state is local to the component until submission triggers a mutation.

---

## 5. Deployment Pipeline (CI/CD)

### 5.1 Overview

```text
Local Commit → GitHub PR → GitHub Actions (CI) → Merge to main
  → GitHub Actions (CD) → Build Docker Image → Push to Amazon ECR
  → Deploy to ECS (staging) → Smoke Tests → Manual Approval
  → Deploy to ECS (production)
```

### 5.2 Step-by-Step Flow

1. **Local Commit**
   ```bash
   git checkout -b feature/property-search-filters
   git add .
   git commit -m "feat: add amenity filter to property search"
   git push origin feature/property-search-filters
   ```

2. **Open Pull Request** against `main` on GitHub.

3. **CI Runs Automatically** (`.github/workflows/backend-ci.yml` / `frontend-ci.yml`) on every push and PR:
   - Checkout code
   - Backend: `mvn -B verify` (compiles, runs unit + integration tests, checks code coverage threshold)
   - Frontend: `npm ci && npm run lint && npm run test && npm run build`
   - Static analysis (e.g., SonarCloud or Checkstyle) reports back to the PR

4. **Code Review & Approval** — at least one required reviewer approval + all CI checks green before merge is allowed (branch protection rule on `main`).

5. **Merge to `main`** triggers the deployment workflow (`.github/workflows/deploy.yml`):
   ```yaml
   on:
     push:
       branches: [main]
   ```

6. **Build & Push Docker Image**
   ```bash
   docker build -t ratemyrental-api:${{ github.sha }} ./backend
   docker tag ratemyrental-api:${{ github.sha }} $ECR_REPO:${{ github.sha }}
   docker push $ECR_REPO:${{ github.sha }}
   ```

7. **Run Flyway Migrations** against the target RDS instance as a pre-deploy CI step (using a scoped migration-only DB credential).

8. **Deploy to Staging (ECS)** — GitHub Actions updates the ECS task definition with the new image tag and triggers a rolling service update:
   ```bash
   aws ecs update-service \
     --cluster ratemyrental-staging \
     --service ratemyrental-api-service \
     --task-definition ratemyrental-api:${{ github.sha }} \
     --force-new-deployment
   ```

9. **Automated Smoke Tests** run against the staging environment (health check, login flow, search endpoint) to catch obvious regressions before production.

10. **Manual Approval Gate** — a designated reviewer approves the production deployment via a GitHub Environments protection rule.

11. **Deploy to Production (ECS)** — same `update-service` command targeting the `ratemyrental-production` cluster; ECS performs a rolling deployment with health-check-gated task replacement to ensure zero downtime.

12. **Post-Deploy Verification** — CloudWatch alarms and dashboards are monitored for elevated error rates or latency spikes for a defined bake-in period (e.g., 15 minutes) before the pipeline marks the release complete.

### 5.3 Rollback Strategy

If a production deployment introduces regressions:

```bash
aws ecs update-service \
  --cluster ratemyrental-production \
  --service ratemyrental-api-service \
  --task-definition ratemyrental-api:<previous-known-good-sha> \
  --force-new-deployment
```

Database migrations are written to be **backward-compatible** (additive changes preferred; destructive changes are split across multiple releases) so that rolling back the application image does not require an immediate corresponding down-migration.

### 5.4 Frontend Deployment

The React build (`npm run build`) output is synced to the production S3 bucket and CloudFront cache is invalidated as the final step of the same `deploy.yml` workflow:

```bash
aws s3 sync ./frontend/dist s3://ratemyrental-frontend-prod --delete
aws cloudfront create-invalidation \
  --distribution-id $CLOUDFRONT_DISTRIBUTION_ID \
  --paths "/*"
```
