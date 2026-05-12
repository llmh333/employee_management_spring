CREATE TABLE employees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL UNIQUE,
    hire_date DATE NOT NULL,
    status VARCHAR(255) NOT NULL,
    notes VARCHAR(500),
    user_id VARCHAR(255) UNIQUE,
    position_id BIGINT,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    CONSTRAINT fk_emp_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_emp_pos FOREIGN KEY (position_id) REFERENCES positions(id) ON DELETE SET NULL
);

CREATE INDEX idx_employees_status ON employees(status);
CREATE INDEX idx_employees_hire_date ON employees(hire_date);
CREATE INDEX idx_employees_pos ON employees(position_id);
