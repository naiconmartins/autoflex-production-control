WITH u AS (
INSERT INTO users(email, password_hash, first_name, last_name, active, created_at)
VALUES ('adm@autoflex.com', '$2a$10$7RI3TeWZC47XYC2g3x92luLY75IOV9PAWN53nY54eZ/Dfm2XDvJ5S', 'Amanda', 'Ribeiro', true, now())
    RETURNING id
    )
INSERT INTO user_roles(user_id, role)
SELECT id, 'ADMIN' FROM u;
