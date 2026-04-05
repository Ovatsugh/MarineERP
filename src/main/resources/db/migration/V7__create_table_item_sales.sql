CREATE TABLE item_sales (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    sale_id UUID REFERENCES sales(id) ON DELETE CASCADE,
    product_id UUID REFERENCES products(id),
    quantity INTEGER,
    amount NUMERIC(19,2)
);
