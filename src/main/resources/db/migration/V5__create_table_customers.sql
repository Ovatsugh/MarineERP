CREATE TABLE customers (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    user_id UUID REFERENCES users(id),
    phone VARCHAR(20) NOT NULL,
    cpf VARCHAR(255) NOT NULL UNIQUE
);
