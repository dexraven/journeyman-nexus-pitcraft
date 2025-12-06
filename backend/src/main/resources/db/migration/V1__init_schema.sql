-- 1. Main table for tracking cooks
CREATE TABLE meat_session (
    id VARCHAR(36) PRIMARY KEY,
    meat_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    weight DOUBLE PRECISION,
    start_time TIMESTAMP,
    serving_time TIMESTAMP,
    alert_sent BOOLEAN DEFAULT FALSE
);

-- 2. Index for faster lookups (since we often query "getActive" / "COOKING")
CREATE INDEX idx_meat_session_status ON meat_session(status);

-- 3. (Optional) Table for temperature history
CREATE TABLE temperature_log (
    id VARCHAR(36) PRIMARY KEY,
    session_id VARCHAR(36) NOT NULL,
    degrees_fahrenheit DOUBLE PRECISION NOT NULL,
    log_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_session FOREIGN KEY (session_id) REFERENCES meat_session(id) ON DELETE CASCADE
);

-- 4. Index for sorting logs by time (useful for finding "stall")
CREATE INDEX idx_temp_log_session_time ON temperature_log(session_id, log_time);