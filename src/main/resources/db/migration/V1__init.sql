-- V1__init.sql
-- Initial schema for portfolio-api
-- Creates contacts and page_events tables

CREATE TABLE IF NOT EXISTS contacts (
    id          BIGSERIAL       PRIMARY KEY,
    name        VARCHAR(100)    NOT NULL,
    email       VARCHAR(254)    NOT NULL,
    message     TEXT            NOT NULL,
    ip_address  VARCHAR(45),
    created_at  TIMESTAMP       NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_contacts_email      ON contacts (email);
CREATE INDEX IF NOT EXISTS idx_contacts_created_at ON contacts (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_contacts_ip_address ON contacts (ip_address);

CREATE TABLE IF NOT EXISTS page_events (
    id          BIGSERIAL       PRIMARY KEY,
    event_type  VARCHAR(30)     NOT NULL,
    page        VARCHAR(512)    NOT NULL,
    referrer    VARCHAR(1024),
    user_agent  VARCHAR(512),
    created_at  TIMESTAMP       NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_page_events_event_type  ON page_events (event_type);
CREATE INDEX IF NOT EXISTS idx_page_events_page        ON page_events (page);
CREATE INDEX IF NOT EXISTS idx_page_events_created_at  ON page_events (created_at DESC);
