CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,

                       uuid UUID NOT NULL DEFAULT gen_random_uuid(),

                       first_name VARCHAR(100) NOT NULL,
                       last_name VARCHAR(100),

                       email VARCHAR(255) NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,

                       phone_number VARCHAR(20),

                       profile_image_url TEXT,

                       role VARCHAR(20) NOT NULL DEFAULT 'USER',

                       status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

                       email_verified BOOLEAN NOT NULL DEFAULT FALSE,

                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       CONSTRAINT uq_users_uuid UNIQUE (uuid),
                       CONSTRAINT uq_users_email UNIQUE (email),

                       CONSTRAINT chk_users_role
                           CHECK (role IN ('USER', 'ADMIN')),

                       CONSTRAINT chk_users_status
                           CHECK (status IN ('ACTIVE', 'BLOCKED', 'DELETED'))
);
