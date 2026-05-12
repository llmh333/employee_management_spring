DELIMITER //

CREATE PROCEDURE sp_get_department_stats()
BEGIN
    SELECT 
        d.id AS department_id,
        d.name AS department_name,
        COUNT(e.id) AS total_employees,
        SUM(CASE WHEN e.status = 'ACTIVE' THEN 1 ELSE 0 END) AS active_employees,
        AVG(p.base_salary) AS average_salary
    FROM departments d
    LEFT JOIN positions p ON d.id = p.department_id
    LEFT JOIN employees e ON p.id = e.position_id
    GROUP BY d.id, d.name;
END //

DELIMITER ;
