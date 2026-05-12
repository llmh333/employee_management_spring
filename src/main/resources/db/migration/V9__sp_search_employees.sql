DELIMITER //

CREATE PROCEDURE sp_search_employees(IN search_keyword VARCHAR(255))
BEGIN
    SELECT 
        e.employee_code,
        u.first_name,
        u.last_name,
        u.email,
        p.title AS position_title,
        d.name AS department_name,
        e.status
    FROM employees e
    JOIN users u ON e.user_id = u.id
    JOIN positions p ON e.position_id = p.id
    JOIN departments d ON p.department_id = d.id
    WHERE 
        search_keyword IS NULL 
        OR search_keyword = ''
        OR LOWER(u.first_name) LIKE LOWER(CONCAT('%', search_keyword, '%'))
        OR LOWER(u.last_name) LIKE LOWER(CONCAT('%', search_keyword, '%'))
        OR LOWER(e.employee_code) LIKE LOWER(CONCAT('%', search_keyword, '%'));
END //

DELIMITER ;
