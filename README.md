# Employee Management Spring Boot

## Project Mandates

This project follows **Senior Backend Standards** and is designed for **High Scalability (50,000+ employees)**.

### Key Features for Scale & Quality:
- **Optimistic Locking**: Prevents concurrent update issues using JPA `@Version`.
- **Distributed Safe IDs**: Employee codes generated using collision-resistant strategies.
- **Full Auditing**: Tracking `createdBy`, `updatedBy` alongside timestamps.
- **MapStruct Integration**: Type-safe, high-performance object mapping.
- **Database Indexing**: Optimized for high-volume search and filtering.
- **Granular RBAC**: Security enforced at the service and controller levels.

Refer to `.gemini/GEMINI.md` for the full technical mandates.
