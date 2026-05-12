# Senior Backend & High-Scale Rules (50k+ Records)

Specific guidelines for building scalable, maintainable, and robust backend systems for the Employee Management project.

## 1. High-Scale Data Handling (50k+ Records)

- **Efficient Pagination**: ALWAYS use Spring Data `Pageable`. Never fetch all records and filter in memory.
- **Indexing Strategy**: 
    - Every field used in `WHERE`, `ORDER BY`, or `JOIN` must be indexed.
    - Fields to index: `employee_code`, `hire_date`, `status`, `user_id`, `position_id`.
- **Search Optimization**:
    - Avoid leading wildcards in `LIKE` queries (e.g., `%keyword%`) if possible, as they bypass B-Tree indexes.
    - For scale > 100k, prefer a dedicated search engine (ElasticSearch) or database-specific Full-Text Search (FTS).
- **DTO Projections**: For search/list results, use interface-based or class-based projections to fetch only necessary columns, reducing network overhead and memory usage.

## 2. Distributed System Safety

- **Unique Code Generation**: NEVER use `AtomicLong` or in-memory counters for business-critical codes (like Employee Codes).
    - **Senior Standard**: Use a Database Sequence (SQL) or a distributed ID generator (e.g., Snowflake ID, UUID) to prevent collisions in multi-instance environments.
- **Concurrency Control**: Implement **Optimistic Locking** using a `@Version` column in entities to prevent lost updates during concurrent edits.

## 3. Senior Backend Architecture

- **Strict DTO Mapping**:
    - Entities must NEVER be exposed to the API layer.
    - Use **MapStruct** for all Entity <-> DTO conversions. Manual mapping is discouraged for maintainability.
- **Auditing & Traceability**:
    - CMS entities (Employee, Position, Department) MUST extend `FullAuditing` to track `createdBy`, `updatedBy`, `createdAt`, and `updatedAt`.
- **Soft Deletion**: Use a `deleted` flag (boolean) or `deleted_at` (timestamp) instead of hard deletion (`DELETE`) for sensitive business data.
- **Validation**:
    - Use `@Valid` and JSR-303 annotations on all request DTOs.
    - Business logic validation (e.g., "hire date cannot be in the future") belongs in the Service Layer.

## 4. Performance & Caching

- **Redis Integration**:
    - Cache frequently accessed lookup data (Positions, Departments) with a TTL.
    - Cache individual Employee profiles by ID for high-frequency "Read Detail" operations.
    - Use `@Cacheable`, `@CachePut`, and `@CacheEvict` for clean implementation.
- **Transactional Integrity**:
    - Use `@Transactional(readOnly = true)` for all read-only service methods to optimize performance.
    - Keep write transactions as short as possible.

## 5. Security & Error Handling

- **Granular RBAC**: Use Spring Security `@PreAuthorize` on controller methods or service methods to enforce strict Role-Based Access Control.
- **Standardized Error Responses**:
    - All exceptions must be caught by `GlobalHandlerException`.
    - Error responses must follow the `RestData` structure: `status`, `message`, `data`.
    - Never expose internal stack traces or database errors to the client.
