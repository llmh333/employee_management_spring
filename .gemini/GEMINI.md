# Employee Management System — Backend Mandates

This document defines the core architecture, security, and performance standards for this Spring Boot project. These mandates take precedence over general defaults.

## Core Standards

- **Scalability**: All features must be designed for a minimum of 50,000 employees. Efficiency in database queries and caching is mandatory.
- **Seniority**: Code must reflect senior backend practices: type safety, automated testing, distributed system safety, and clean abstractions.
- **Reference**: Detailed rules can be found in [.gemini/rules/senior-backend-scale.md](.gemini/rules/senior-backend-scale.md).

## Critical Implementation Mandates

1. **ID & Code Generation**:
   - Use UUIDs for user identifiers.
   - Use Database Sequences for sequential business codes (e.g., Employee Code).
   - NEVER use in-memory counters (`AtomicLong`) for distributed entities.

2. **Persistence Layer**:
   - Every CMS entity must extend `FullAuditing` and have `@Version` for optimistic locking.
   - Use `MapStruct` for mapping. Manual mapping in services is prohibited.
   - All list APIs must implement pagination via `Pageable`.

3. **Performance**:
   - Cache results for "Get by ID" and lookup tables using Redis.
   - Use `@Transactional(readOnly = true)` for all read-only methods.
   - Avoid `EAGER` fetching; use `JOIN FETCH` or Entity Graphs for N+1 prevention.

4. **Security**:
   - Enforce RBAC using `@PreAuthorize`.
   - Admin operations (Write/Delete) must be strictly audited.
