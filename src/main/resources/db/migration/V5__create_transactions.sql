CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    reference_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    date DATE NOT NULL,
    note TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX idx_transactions_reference_id ON transactions(reference_id);