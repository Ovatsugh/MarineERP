ALTER TABLE users
ADD COLUMN company_id UUID REFERENCES companies(id);
