CREATE TABLE workers (
                         id UUID PRIMARY KEY,
                         user_id UUID NOT NULL UNIQUE,
                         first_name VARCHAR(100) NOT NULL,
                         last_name VARCHAR(100) NOT NULL,
                         bio VARCHAR(1000),
                         skills TEXT[],
                         hourly_rate DECIMAL(10,2),
                         experience_level VARCHAR(20),
                         availability_status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
                         portfolio_url VARCHAR(500),
                         location_city VARCHAR(100),
                         location_country VARCHAR(100),
                         location_lat DOUBLE PRECISION,
                         location_lng DOUBLE PRECISION,
                         total_earnings DECIMAL(12,2) DEFAULT 0,
                         completed_gigs INTEGER DEFAULT 0,
                         avg_rating DECIMAL(3,2) DEFAULT 0,
                         total_reviews INTEGER DEFAULT 0,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_workers_user_id ON workers(user_id);
CREATE INDEX idx_workers_availability ON workers(availability_status);
CREATE INDEX idx_workers_location ON workers(location_lat, location_lng);
CREATE INDEX idx_workers_skills ON workers USING GIN(skills);