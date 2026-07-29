# RateMyRental - Product Specification (spec.md)

Version: 1.0
Status: Draft
Author: Shrishail
Last Updated: July 2026

---

# 1. Overview

RateMyRental is a community-driven platform that helps tenants make informed rental decisions by providing honest reviews, ratings, and insights about apartments, standalone buildings, gated communities, and landlords.

The goal is to improve transparency in the rental market by allowing current and former tenants to share their experiences.

---

# 2. Problem Statement

Finding a rental property is difficult because:

- Rental websites only show positive information.
- Hidden issues are discovered only after moving in.
- No reliable source exists for tenant experiences.
- Landlord behavior is unknown.
- Maintenance quality cannot be verified.
- Water, electricity, parking, and security issues are hidden.

RateMyRental solves this by creating trusted tenant reviews.

---

# 3. Vision

Become the "Google Reviews" for rental properties.

Users should be able to know everything about a property before paying a deposit.

---

# 4. Goals

Primary Goals

- Property Reviews
- Building Ratings
- Landlord Ratings
- Verified Tenant Reviews
- Search Nearby Properties
- Property Comparison

Future Goals

- AI Review Summaries
- Rent Prediction
- Complaint Analytics
- Fraud Detection
- Rental Price Trends

---

# 5. Target Users

Primary

- Tenants
- Students
- Families
- Working Professionals

Secondary

- Property Owners
- Property Managers
- Real Estate Agents

---

# 6. User Roles

### Guest

Can

- Search properties
- View ratings
- Read reviews
- Compare properties

Cannot

- Write reviews
- Like reviews
- Report reviews

---

### Registered User

Can

- Login
- Submit review
- Edit own review
- Delete own review
- Upload images
- Rate landlord
- Save favorite properties

---

### Admin

Can

- Remove fake reviews
- Moderate content
- Ban users
- Approve reported properties
- Manage landlords

---

# 7. Core Features

## 7.1 Property Search

Search by

- Property Name
- Building Name
- Apartment Name
- Area
- City
- Pincode

Filters

- Rent
- BHK
- Rating
- Parking
- Lift
- Security
- Pet Friendly
- Furnished
- Water Availability

---

## 7.2 Property Detail Page

Displays

- Photos
- Address
- Average Rating
- Review Count
- Amenities
- Monthly Rent
- Deposit
- Landlord Rating
- Map Location

Sections

Overview

Reviews

Amenities

Comparison

Nearby

---

## 7.3 Ratings

Overall Rating

Sub Ratings

- Security
- Water Supply
- Cleanliness
- Maintenance
- Parking
- Internet
- Noise Level
- Lift
- Power Backup
- Neighborhood
- Value for Money

Rating Scale

1–5 Stars

---

## 7.4 Reviews

Review Contains

- Title
- Description
- Pros
- Cons
- Stay Duration
- Move-in Year
- Anonymous Option
- Images

Users can

- Like
- Report
- Edit
- Delete

---

## 7.5 Compare Properties

Compare up to

4 properties

Compare

- Rent
- Deposit
- Ratings
- Amenities
- Security
- Parking
- Water
- Reviews

---

## 7.6 Favorites

Users can

- Bookmark property
- Remove bookmark
- View saved list

---

## 7.7 Authentication

Signup

Login

Forgot Password

JWT Authentication

Refresh Token

Google Login (Future)

---

# 8. Functional Requirements

FR-001

User Registration

FR-002

User Login

FR-003

Search Property

FR-004

View Property

FR-005

Add Review

FR-006

Edit Review

FR-007

Delete Review

FR-008

Upload Images

FR-009

Rate Property

FR-010

Compare Properties

FR-011

Favorite Property

FR-012

Report Review

FR-013

Admin Moderation

FR-014

Search Nearby Properties

---

# 9. Non Functional Requirements

Availability

99.9%

Performance

Search response

<500 ms

Security

JWT

HTTPS

Password Encryption

Scalability

Horizontal scaling

Reliability

No data loss

Accessibility

Mobile Friendly

---

# 10. Tech Stack

Backend

- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA
- Hibernate
- Maven

Database

- PostgreSQL

Authentication

- JWT

Migration

- Flyway

Documentation

- Swagger/OpenAPI

Frontend

- React
- Tailwind CSS

Cloud

- AWS

Storage

- Amazon S3

Container

- Docker

Version Control

- GitHub

---

# 11. Database Entities

User

Property

Review

Landlord

PropertyImage

Favorite

ReviewImage

ReviewLike

Report

Amenity

City

---

# 12. REST APIs (Initial)

Authentication

POST /api/auth/register

POST /api/auth/login

POST /api/auth/refresh

Property

GET /api/properties

GET /api/properties/{id}

POST /api/properties

PUT /api/properties/{id}

DELETE /api/properties/{id}

Review

GET /api/properties/{id}/reviews

POST /api/properties/{id}/reviews

PUT /api/reviews/{id}

DELETE /api/reviews/{id}

Favorite

POST /api/favorites

DELETE /api/favorites/{id}

GET /api/favorites

Comparison

POST /api/compare

---

# 13. MVP Scope

Included

✅ User Registration

✅ Login

✅ JWT Authentication

✅ Property Search

✅ Property Details

✅ Reviews

✅ Ratings

✅ Favorites

Not Included

❌ AI Summary

❌ Rent Prediction

❌ Chat

❌ Notifications

❌ Mobile App

---

# 14. Future Roadmap

Phase 2

- AI Generated Review Summary
- Review Sentiment Analysis
- Landlord Verification
- Image Moderation

Phase 3

- Mobile Apps
- Maps Integration
- Nearby Schools
- Nearby Hospitals
- Rental Price Trends
- AI Recommendations

Phase 4

- Property Claim
- Owner Dashboard
- Premium Subscription
- Verified Properties

---

# 15. Success Metrics

- Active Users
- Reviews Submitted
- Average Rating Accuracy
- Monthly Searches
- User Retention
- Review Approval Rate
- Average API Response Time
- Search Success Rate

---

# 16. Project Structure

ratemyrental

backend/

frontend/

docs/

docker/

database/

.github/

README.md

spec.md

---

# 17. License

MIT License

---

# 18. Contributors

Founder

Shrishail

Powered by

Spring Boot
React
PostgreSQL
AWS