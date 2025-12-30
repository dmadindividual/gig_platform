CREATE TABLE email_verification_tokens (
                                           id UUID PRIMARY KEY,
                                           user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                           token VARCHAR(255) NOT NULL UNIQUE,
                                           expiry_date TIMESTAMP NOT NULL,
                                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                           updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                           version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_verification_token ON email_verification_tokens(token);
CREATE INDEX idx_verification_user_id ON email_verification_tokens(user_id);