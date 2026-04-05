CREATE TABLE sales (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    company_id UUID REFERENCES companies(id),
    user_id UUID REFERENCES users(id),
    customer_id UUID REFERENCES customers(id),
    amount NUMERIC(19,2),
    notes TEXT
);
