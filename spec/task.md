# RateMyRental — Agile Task Breakdown

**Version:** 1.0
**Last Updated:** 2026-07-30
**Format:** Phases → Epics → User Stories → Developer Tasks

---

## 1. Phases

### Phase 1 — MVP (Minimum Viable Product)
Goal: Ship a functional tenant-facing app where users can register, search properties, view details, and read/submit ratings & reviews.

- Epic: User Authentication & Onboarding
- Epic: Property Search
- Epic: Property Detail Page
- Epic: Ratings & Reviews
- Epic: Core Infrastructure & DevOps

### Phase 2 — Growth Features
Goal: Deepen engagement and add differentiating features once the MVP is validated with real users.

- Epic: Compare Properties
- Epic: Saved Properties / Watchlist
- Epic: Review Moderation & Trust Signals
- Epic: Advanced Search (map view, geolocation, saved searches)
- Epic: Notifications (email/push for new reviews, price changes)
- Epic: Landlord/Property Owner Response to Reviews

### Phase 3 — Scale & Optimization
Goal: Harden the platform for scale, improve retention, and expand monetization/ecosystem features.

- Epic: Performance & Scalability (caching, read replicas, CDN tuning)
- Epic: Analytics & Recommendation Engine (personalized property suggestions)
- Epic: Mobile App Parity (React Native or PWA enhancements)
- Epic: Third-Party Integrations (mortgage/rent calculators, virtual tours)
- Epic: Multi-language & Localization Support
- Epic: Admin Dashboard & Content Moderation Tooling

---

## 2. Phase 1 — Epics and User Stories

### Epic 1: User Authentication & Onboarding

- **US-101**: As a new user, I want to register with my email and password, so that I can create an account.
- **US-102**: As a registered user, I want to log in with my credentials, so that I can access personalized features.
- **US-103**: As a logged-in user, I want my session to stay active via token refresh, so that I don't get logged out unexpectedly.
- **US-104**: As a user, I want to reset my forgotten password via email, so that I can regain access to my account.
- **US-105**: As a user, I want to log out, so that my session is securely terminated on shared devices.

### Epic 2: Property Search

- **US-201**: As a tenant, I want to search properties by city/locality, so that I can find listings in my desired area.
- **US-202**: As a tenant, I want to filter search results by rent range, bedrooms, and amenities, so that I can narrow down relevant listings.
- **US-203**: As a tenant, I want to sort search results by rating, rent, or newest, so that I can prioritize what matters to me.
- **US-204**: As a tenant, I want to paginate through search results, so that the page loads quickly and remains usable.
- **US-205**: As a tenant, I want to see a summary card (image, rent, rating, bedrooms) for each property in results, so that I can quickly evaluate options.

### Epic 3: Property Detail Page

- **US-301**: As a tenant, I want to view full property details (description, amenities, address, images), so that I can evaluate the listing.
- **US-302**: As a tenant, I want to view a photo gallery for a property, so that I can visually assess the space.
- **US-303**: As a tenant, I want to see the property's location on a map, so that I understand its surroundings.
- **US-304**: As a tenant, I want to see the average rating and review count on the detail page, so that I can gauge tenant satisfaction at a glance.

### Epic 4: Ratings & Reviews

- **US-401**: As a logged-in tenant, I want to submit a star rating and written review for a property, so that I can share my experience.
- **US-402**: As a logged-in tenant, I want to attach photos to my review, so that I can provide visual evidence of my experience.
- **US-403**: As a tenant, I want to view all reviews for a property, sorted by most recent or highest rated, so that I can make an informed decision.
- **US-404**: As a tenant, I want to see my review flagged as "pending moderation" if it fails an automated content check, so that I understand why it's not yet visible.
- **US-405**: As a tenant, I want to edit or delete my own review, so that I can correct mistakes or remove outdated feedback.

### Epic 5: Core Infrastructure & DevOps

- **US-501**: As a developer, I want a CI pipeline that runs tests on every PR, so that regressions are caught before merge.
- **US-502**: As a developer, I want Dockerized local development, so that environment setup is consistent across the team.
- **US-503**: As a developer, I want Flyway-managed schema migrations, so that database changes are versioned and repeatable across environments.
- **US-504**: As a DevOps engineer, I want automated deployments to staging and production via GitHub Actions, so that releases are fast and low-risk.

---

## 3. Developer Task Breakdown — Core User Story

### Selected Story: **US-401** — "As a logged-in tenant, I want to submit a star rating and written review for a property, so that I can share my experience."

This story is chosen for granular breakdown because it spans the full stack: database schema, backend service/security logic, S3 integration, and frontend form + state management — mirroring the "most complex flow" documented in `architecture.md`.

#### Backend Tasks

- [ ] **DB-401.1**: Create Flyway migration `V2__add_reviews.sql` defining the `reviews` table (id, property_id FK, user_id FK, rating, review_text, status, created_at, updated_at).
- [ ] **DB-401.2**: Create Flyway migration for `review_images` table (id, review_id FK, s3_key, created_at).
- [ ] **DB-401.3**: Add `avg_rating` and `review_count` columns to `properties` table via migration (if not already present from initial schema).
- [ ] **BE-401.4**: Create `Review` and `ReviewImage` JPA entities with appropriate `@ManyToOne`/`@OneToMany` mappings.
- [ ] **BE-401.5**: Create `ReviewRepository` (Spring Data JPA) with a query method for fetching reviews by `propertyId` with pagination and sort support.
- [ ] **BE-401.6**: Create `SubmitReviewRequest` DTO with Bean Validation annotations (`@Min(1) @Max(5)` on rating, `@Size(min=20, max=2000)` on reviewText).
- [ ] **BE-401.7**: Create `ReviewResponse` DTO mapping entity fields to the API contract defined in `engineering.md`.
- [ ] **BE-401.8**: Implement `S3StorageService.generatePresignedUploadUrl(userId, fileName)` for direct-to-S3 photo uploads.
- [ ] **BE-401.9**: Implement `ModerationClient` service (interface + REST client) to call the content moderation API with review text/images.
- [ ] **BE-401.10**: Implement `ReviewService.submitReview(propertyId, userId, request)`:
  - Validate property exists.
  - Call `ModerationClient` to check content.
  - Persist `Review` (+ `ReviewImage` rows) inside a `@Transactional` method.
  - Recalculate and update `properties.avg_rating` and `review_count` in the same transaction.
  - Return appropriate status (`APPROVED` vs `PENDING_REVIEW`).
- [ ] **BE-401.11**: Implement Redis cache invalidation for `property:{id}` and related search cache keys after a successful review write.
- [ ] **BE-401.12**: Implement `ReviewController` with `POST /api/v1/properties/{propertyId}/reviews` and `GET /api/v1/properties/{propertyId}/reviews` endpoints.
- [ ] **BE-401.13**: Secure the `POST` endpoint with Spring Security (`@PreAuthorize("isAuthenticated()")`) so only logged-in users can submit reviews.
- [ ] **BE-401.14**: Add `GlobalExceptionHandler` cases for validation errors, property-not-found, and moderation-service-unavailable scenarios.
- [ ] **BE-401.15**: Annotate `ReviewController` methods with OpenAPI/Swagger annotations (`@Operation`, `@ApiResponse`) for auto-generated docs.
- [ ] **TEST-401.16**: Write unit tests for `ReviewService` covering: happy path, moderation-flagged path, and concurrent-review aggregate-consistency scenario.
- [ ] **TEST-401.17**: Write integration test (`@SpringBootTest` + Testcontainers Postgres) for the full `POST /reviews` flow, asserting DB state and response payload.

#### Frontend Tasks

- [ ] **FE-401.18**: Build `ReviewForm.tsx` component with star-rating input, textarea (character counter, 20–2000 chars), and photo upload widget.
- [ ] **FE-401.19**: Define Zod validation schema matching backend constraints (rating 1–5, reviewText length).
- [ ] **FE-401.20**: Wire `ReviewForm` with **React Hook Form** for local form state and validation error display.
- [ ] **FE-401.21**: Implement `reviewApi.ts` — `getPresignedUploadUrl()`, `uploadToS3(url, file)`, and `submitReview(propertyId, payload)` functions.
- [ ] **FE-401.22**: Implement photo upload flow: request pre-signed URL → PUT file directly to S3 → collect resulting object keys → include in review submission payload.
- [ ] **FE-401.23**: Implement `useSubmitReview` mutation hook (React Query) with success/error handling and cache invalidation for `["property", id]` and `["reviews", id]`.
- [ ] **FE-401.24**: Add optimistic UI state (disable submit button, show spinner) while the mutation is in flight.
- [ ] **FE-401.25**: Display success toast/banner on `201` response; display "pending moderation" banner on `202` response.
- [ ] **FE-401.26**: Add the `ReviewForm` to `PropertyDetailPage.tsx` behind an auth-gate (prompt login if unauthenticated user clicks "Write a Review").
- [ ] **FE-401.27**: Build `ReviewList.tsx` component to render paginated reviews with sort control (Most Recent / Highest Rated).
- [ ] **TEST-401.28**: Write component tests (React Testing Library) for `ReviewForm` covering validation errors and successful submission.
- [ ] **TEST-401.29**: Write an end-to-end test (Playwright/Cypress) simulating: log in → navigate to property → submit review → assert it appears in the review list.

#### Cross-Cutting / DevOps Tasks

- [ ] **OPS-401.30**: Add moderation API key and endpoint URL to Secrets Manager / `.env` templates for all environments.
- [ ] **OPS-401.31**: Update Swagger/OpenAPI spec snapshot in CI to catch unintended contract-breaking changes to the reviews endpoints.
- [ ] **OPS-401.32**: Add a CloudWatch alarm on elevated `5xx` rate for the `/reviews` endpoint post-deploy.

---

## 4. Definition of Done (DoD)

A pull request may be merged into `main` **only if all of the following are true**:

### 4.1 Code Quality
- [ ] Code follows the project's existing style/conventions (backend: Java/Spring conventions + Checkstyle rules pass; frontend: ESLint + Prettier pass with zero errors).
- [ ] No commented-out code, `console.log`/`System.out.println` debug statements, or unused imports remain.
- [ ] No new compiler warnings introduced.

### 4.2 Testing
- [ ] All new business logic has unit test coverage (backend: JUnit 5 + Mockito; frontend: Vitest/Jest + React Testing Library).
- [ ] Critical flows (e.g., review submission, search, auth) have integration or E2E test coverage where applicable.
- [ ] Overall code coverage does not drop below the project threshold (e.g., 80% line coverage on backend service classes).
- [ ] All existing tests pass locally and in CI — no skipped/disabled tests without a linked ticket explaining why.

### 4.3 Functionality & Contracts
- [ ] The implementation matches the acceptance criteria of the linked user story exactly.
- [ ] Any new/changed REST endpoint has an updated OpenAPI/Swagger annotation and matches the documented contract in `engineering.md` (or that doc is updated in the same PR).
- [ ] Database changes are implemented as a new Flyway migration file (never editing an already-applied migration).
- [ ] Migrations are backward-compatible with the currently deployed application version (additive-first strategy per the rollback policy).

### 4.4 Security
- [ ] All new endpoints have explicit authorization rules (`@PreAuthorize` or equivalent) — nothing is left implicitly public.
- [ ] User input is validated server-side (Bean Validation) regardless of client-side validation.
- [ ] No secrets, credentials, or API keys are hard-coded or committed to the repository.
- [ ] Dependencies introduced in the PR have no known critical/high vulnerabilities (checked via `mvn dependency-check` / `npm audit`).

### 4.5 Performance & Observability
- [ ] New endpoints that query the database use appropriate indexes (verified via `EXPLAIN ANALYZE` for non-trivial queries).
- [ ] Logging is added at appropriate levels (INFO for key business events, ERROR for failures) without logging sensitive data (passwords, tokens, PII).
- [ ] Any new external service call (S3, moderation API, geocoding) has a timeout and failure-handling strategy.

### 4.6 Documentation & Review
- [ ] PR description clearly explains **what** changed and **why**, and links the relevant user story/ticket.
- [ ] Any architectural or API contract changes are reflected in `architecture.md` / `engineering.md` in the same PR.
- [ ] At least one other engineer has reviewed and approved the PR.
- [ ] All CI checks (build, lint, test, security scan) are green before merge.

### 4.7 Deployment Readiness
- [ ] Feature is verified working in the staging environment (not just locally) before being considered done.
- [ ] Any required environment variables or secrets for the feature are documented and configured in staging/production.
- [ ] Rollback plan is implicitly satisfied (migration is additive, or a documented manual rollback step exists).
