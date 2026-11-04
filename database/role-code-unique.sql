-- Existing MySQL database: inspect and resolve duplicates before applying the ALTER.
-- SELECT code, COUNT(*) FROM role GROUP BY code HAVING COUNT(*) > 1;
ALTER TABLE role ADD CONSTRAINT uk_role_code UNIQUE (code);
