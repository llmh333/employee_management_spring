-- Add auditing and version columns to departments
ALTER TABLE departments ADD COLUMN created_by VARCHAR(255);
ALTER TABLE departments ADD COLUMN updated_by VARCHAR(255);
ALTER TABLE departments ADD COLUMN version BIGINT DEFAULT 0;

-- Add auditing and version columns to positions
ALTER TABLE positions ADD COLUMN created_by VARCHAR(255);
ALTER TABLE positions ADD COLUMN updated_by VARCHAR(255);
ALTER TABLE positions ADD COLUMN version BIGINT DEFAULT 0;

-- Add auditing and version columns to employees
ALTER TABLE employees ADD COLUMN created_by VARCHAR(255);
ALTER TABLE employees ADD COLUMN updated_by VARCHAR(255);
ALTER TABLE employees ADD COLUMN version BIGINT DEFAULT 0;
