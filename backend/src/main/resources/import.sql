WITH u AS (
INSERT INTO users(email, password_hash, first_name, last_name, active, created_at)
VALUES ('adm@autoflex.com', '$2a$10$7RI3TeWZC47XYC2g3x92luLY75IOV9PAWN53nY54eZ/Dfm2XDvJ5S', 'Amanda', 'Ribeiro', true, now())
    RETURNING id
    )
INSERT INTO user_roles(user_id, role)
SELECT id, 'ADMIN' FROM u;



-- =====================================================
-- RAW MATERIALS (Matérias-Primas)
-- =====================================================

-- Madeiras
INSERT INTO raw_materials (id, code, name, stock_quantity) VALUES (1, 'MAD-001', 'Pine Wood Board 2.5x30x200cm', 150.00);
INSERT INTO raw_materials (id, code, name, stock_quantity) VALUES (2, 'MAD-002', 'Oak Wood Board 2.5x30x200cm', 80.00);
INSERT INTO raw_materials (id, code, name, stock_quantity) VALUES (3, 'MAD-003', 'MDF Board 15mm 1.80x2.75m', 45.00);
INSERT INTO raw_materials (id, code, name, stock_quantity) VALUES (4, 'MAD-004', 'Plywood 10mm 1.22x2.44m', 60.00);

-- Ferragens
INSERT INTO raw_materials (id, code, name, stock_quantity) VALUES (5, 'FER-001', 'Wood Screws 4x50mm', 5000.00);
INSERT INTO raw_materials (id, code, name, stock_quantity) VALUES (6, 'FER-002', 'Cabinet Hinges 35mm', 800.00);
INSERT INTO raw_materials (id, code, name, stock_quantity) VALUES (7, 'FER-003', 'Drawer Slides 45cm', 400.00);
INSERT INTO raw_materials (id, code, name, stock_quantity) VALUES (8, 'FER-004', 'Cabinet Handles Chrome', 600.00);
INSERT INTO raw_materials (id, code, name, stock_quantity) VALUES (9, 'FER-005', 'Table Legs Metal 70cm', 200.00);

-- Acabamentos
INSERT INTO raw_materials (id, code, name, stock_quantity) VALUES (10, 'ACA-001', 'Wood Varnish Clear 1L', 25.00);
INSERT INTO raw_materials (id, code, name, stock_quantity) VALUES (11, 'ACA-002', 'Wood Stain Walnut 1L', 30.00);
INSERT INTO raw_materials (id, code, name, stock_quantity) VALUES (12, 'ACA-003', 'White Paint 1L', 40.00);
INSERT INTO raw_materials (id, code, name, stock_quantity) VALUES (13, 'ACA-004', 'Wood Glue 500ml', 50.00);
INSERT INTO raw_materials (id, code, name, stock_quantity) VALUES (14, 'ACA-005', 'Sandpaper Assorted Pack', 100.00);

-- Estofamento (para cadeiras)
INSERT INTO raw_materials (id, code, name, stock_quantity) VALUES (15, 'EST-001', 'Foam Padding 5cm Density 28', 80.00);
INSERT INTO raw_materials (id, code, name, stock_quantity) VALUES (16, 'EST-002', 'Fabric Upholstery Beige 1.40m', 120.00);
INSERT INTO raw_materials (id, code, name, stock_quantity) VALUES (17, 'EST-003', 'Fabric Upholstery Gray 1.40m', 95.00);

-- Vidros e Espelhos
INSERT INTO raw_materials (id, code, name, stock_quantity) VALUES (18, 'VID-001', 'Tempered Glass 4mm', 30.00);
INSERT INTO raw_materials (id, code, name, stock_quantity) VALUES (19, 'VID-002', 'Mirror 4mm', 25.00);

-- =====================================================
-- PRODUCTS (Produtos)
-- =====================================================

-- Mesas
INSERT INTO products (id, code, name, price) VALUES (1, 'PROD-001', 'Dining Table Oak 1.80m', 1250.00);
INSERT INTO products (id, code, name, price) VALUES (2, 'PROD-002', 'Coffee Table Pine', 380.00);
INSERT INTO products (id, code, name, price) VALUES (3, 'PROD-003', 'Desk with Drawers', 650.00);

-- Cadeiras
INSERT INTO products (id, code, name, price) VALUES (4, 'PROD-004', 'Upholstered Dining Chair', 285.00);
INSERT INTO products (id, code, name, price) VALUES (5, 'PROD-005', 'Office Chair with Wheels', 420.00);

-- Armários
INSERT INTO products (id, code, name, price) VALUES (6, 'PROD-006', 'Kitchen Cabinet 2 Doors', 580.00);
INSERT INTO products (id, code, name, price) VALUES (7, 'PROD-007', 'Wardrobe 3 Doors with Mirror', 1850.00);
INSERT INTO products (id, code, name, price) VALUES (8, 'PROD-008', 'Bookshelf 5 Shelves', 445.00);

-- Cômodas
INSERT INTO products (id, code, name, price) VALUES (9, 'PROD-009', 'Chest of Drawers 4 Drawers', 520.00);
INSERT INTO products (id, code, name, price) VALUES (10, 'PROD-010', 'Bedside Table 2 Drawers', 195.00);

-- =====================================================
-- PRODUCT RAW MATERIALS (Composição dos Produtos)
-- =====================================================

-- PROD-001: Dining Table Oak 1.80m
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (1, 2, 8.00);  -- Oak Wood Board
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (1, 9, 4.00);  -- Table Legs
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (1, 5, 32.00); -- Wood Screws
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (1, 11, 0.50); -- Wood Stain
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (1, 10, 0.30); -- Wood Varnish
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (1, 13, 0.20); -- Wood Glue
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (1, 14, 1.00); -- Sandpaper

-- PROD-002: Coffee Table Pine
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (2, 1, 5.00);  -- Pine Wood Board
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (2, 9, 4.00);  -- Table Legs
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (2, 5, 24.00); -- Wood Screws
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (2, 10, 0.25); -- Wood Varnish
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (2, 13, 0.15); -- Wood Glue
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (2, 14, 1.00); -- Sandpaper

-- PROD-003: Desk with Drawers
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (3, 3, 2.00);  -- MDF Board
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (3, 1, 4.00);  -- Pine Wood Board
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (3, 7, 3.00);  -- Drawer Slides
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (3, 8, 3.00);  -- Cabinet Handles
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (3, 5, 40.00); -- Wood Screws
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (3, 12, 0.40); -- White Paint
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (3, 13, 0.25); -- Wood Glue
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (3, 14, 1.00); -- Sandpaper

-- PROD-004: Upholstered Dining Chair
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (4, 1, 3.00);  -- Pine Wood Board
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (4, 15, 1.00); -- Foam Padding
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (4, 16, 0.80); -- Fabric Beige
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (4, 5, 16.00); -- Wood Screws
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (4, 11, 0.15); -- Wood Stain
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (4, 13, 0.10); -- Wood Glue
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (4, 14, 1.00); -- Sandpaper

-- PROD-005: Office Chair with Wheels
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (5, 4, 1.00);  -- Plywood
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (5, 15, 1.50); -- Foam Padding
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (5, 17, 1.00); -- Fabric Gray
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (5, 5, 20.00); -- Wood Screws
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (5, 12, 0.20); -- White Paint
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (5, 13, 0.15); -- Wood Glue

-- PROD-006: Kitchen Cabinet 2 Doors
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (6, 3, 2.00);  -- MDF Board
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (6, 6, 4.00);  -- Cabinet Hinges
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (6, 8, 2.00);  -- Cabinet Handles
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (6, 5, 48.00); -- Wood Screws
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (6, 12, 0.50); -- White Paint
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (6, 13, 0.30); -- Wood Glue
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (6, 14, 1.00); -- Sandpaper

-- PROD-007: Wardrobe 3 Doors with Mirror
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (7, 3, 4.00);  -- MDF Board
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (7, 2, 4.00);  -- Oak Wood Board
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (7, 19, 2.00); -- Mirror
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (7, 6, 6.00);  -- Cabinet Hinges
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (7, 8, 3.00);  -- Cabinet Handles
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (7, 5, 80.00); -- Wood Screws
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (7, 12, 0.80); -- White Paint
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (7, 13, 0.50); -- Wood Glue
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (7, 14, 2.00); -- Sandpaper

-- PROD-008: Bookshelf 5 Shelves
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (8, 3, 2.50);  -- MDF Board
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (8, 1, 2.00);  -- Pine Wood Board
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (8, 5, 60.00); -- Wood Screws
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (8, 12, 0.60); -- White Paint
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (8, 13, 0.35); -- Wood Glue
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (8, 14, 1.00); -- Sandpaper

-- PROD-009: Chest of Drawers 4 Drawers
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (9, 3, 2.00);  -- MDF Board
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (9, 1, 3.00);  -- Pine Wood Board
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (9, 7, 4.00);  -- Drawer Slides
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (9, 8, 4.00);  -- Cabinet Handles
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (9, 5, 55.00); -- Wood Screws
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (9, 12, 0.45); -- White Paint
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (9, 13, 0.30); -- Wood Glue
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (9, 14, 1.00); -- Sandpaper

-- PROD-010: Bedside Table 2 Drawers
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (10, 3, 1.00);  -- MDF Board
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (10, 1, 2.00);  -- Pine Wood Board
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (10, 7, 2.00);  -- Drawer Slides
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (10, 8, 2.00);  -- Cabinet Handles
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (10, 5, 28.00); -- Wood Screws
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (10, 12, 0.25); -- White Paint
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (10, 13, 0.15); -- Wood Glue
INSERT INTO product_raw_materials (product_id, raw_material_id, required_quantity) VALUES (10, 14, 1.00); -- Sandpaper

-- Reset sequences (PostgreSQL)
ALTER SEQUENCE raw_materials_id_seq RESTART WITH 20;
ALTER SEQUENCE products_id_seq RESTART WITH 11;
ALTER SEQUENCE product_raw_materials_id_seq RESTART WITH 100;