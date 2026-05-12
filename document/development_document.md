# Tài Liệu Kỹ Thuật (Development Document)
**Dự án**: Employee Management System (Hệ thống Quản lý Nhân sự)

---

## 1. Công nghệ & Kiến trúc Hệ thống (Tech Stack)
- **Ngôn ngữ**: Java 17
- **Framework Chính**: Spring Boot 3.x
- **Database**: MySQL 8.0
- **Cache / Session**: Redis 7
- **Bảo mật**: Spring Security + JWT (JSON Web Tokens)
- **Database Migration**: Flyway
- **Công cụ hỗ trợ**: MapStruct (Mapping DTO), Lombok (Giảm boilerplate code), Swagger/SpringDoc (Document API).
- **Hạ tầng triển khai**: Docker & Docker Compose.

## 2. Kiến trúc Mã nguồn (Source Code Architecture)
Dự án được cấu trúc theo mô hình **Layered Architecture (N-Tier)**:
1. **Controller Layer (`/controller`)**: Tiếp nhận Request từ Client, validate DTO (sử dụng `@Valid`) và điều hướng tới Service. Trả về format chuẩn thông qua `RestData`.
2. **Service Layer (`/service`)**: Chứa toàn bộ Business Logic. Giao tiếp qua Interface (`IUserService`) và triển khai tại lớp `impl` (`IUserServiceImpl`).
3. **Repository Layer (`/repository`)**: Tương tác với Database thông qua Spring Data JPA và Native Queries.
4. **Domain/Entity Layer (`/domain/entity`)**: Ánh xạ 1-1 với các bảng trong CSDL. Kế thừa các Entity Auditing (`DateAuditing` / `FullAuditing`) để tự động ghi log thời gian.
5. **Config & Exception (`/config`, `/exception`)**: Chứa các cấu hình Bean (Redis, Mail, Security) và Global Exception Handler chặn mọi lỗi tập trung ở một nơi.

> [!TIP]
> **Format Chuẩn Của API (RestData)**
> Bất kể thành công hay thất bại (Kể cả lỗi `500 Internal Server Error`), hệ thống luôn trả về JSON theo một định dạng thống nhất nhờ `GlobalHandlerException`.

## 3. Quản lý Cơ sở dữ liệu (Database Management)
- **Flyway**: JPA `ddl-auto` được đặt thành `validate`. Mọi cấu trúc và thay đổi của Database được quản lý nghiêm ngặt bởi Flyway thông qua các file SQL trong thư mục `src/main/resources/db/migration/`.
  - **V1 -> V7**: Khởi tạo Schema, đánh Index và chèn data mồi (Seed data).
  - **V8 -> V9**: Các Stored Procedure chạy báo cáo và tìm kiếm tốc độ cao.
  - *Developer tuyệt đối không tự ý thay đổi trực tiếp Database. Bất kỳ thay đổi Schema nào phải được tạo thành file `V{version}__mota.sql` mới.*

## 4. Hệ thống Phân trang & Sắp xếp (Pagination & Sorting)
- API tìm kiếm (Ví dụ `getAllUser`) áp dụng tiêu chuẩn phân trang mạnh mẽ, sử dụng `PaginationFullRequestDto`.
- Cấu hình Mapping tên trường từ API (vd: `hireDate`) xuống DB column (vd: `hire_date`) được quản lý trong thư mục `enums` (như `SortByConstant.java`), giúp hạn chế SQL Injection và lỗi cú pháp.

## 5. Hướng dẫn Thiết lập & Chạy dự án (Local Setup)

### 5.1 Cấu hình Biến Môi Trường (Environment Variables)
1. Copy file `.env-example` thành `.env`.
2. Điền các thông tin của Database, Redis, SMTP Email, và chuỗi JWT Secret vào file `.env`.
   
### 5.2 Chạy bằng Docker Compose (Khuyến nghị)
Hệ thống đi kèm một file `docker-compose.yml` cực kỳ mạnh mẽ kết hợp cùng Multi-stage `Dockerfile`. 
Bạn không cần phải tự cài MySQL hay Redis lên máy, chỉ cần chạy:
```bash
docker compose up -d
```
Lệnh này sẽ tự động:
- Build source code ra file `.jar`.
- Kéo image MySQL 8 và Redis 7.
- Khởi động toàn bộ các container và nhúng biến từ file `.env` vào cấu hình của ứng dụng.

### 5.3 Chạy chay trên Local (Dành cho Debug IDE)
Nếu muốn chạy trực tiếp bằng nút `Run` trên IntelliJ / Eclipse:
1. Bật sẵn MySQL và Redis trên máy.
2. Đảm bảo Plugin EnvFile trên IDE đã load file `.env` vào lúc runtime.
3. Chạy lệnh: `./mvnw spring-boot:run`.

> [!WARNING]
> **Lưu ý Security:**  Mật khẩu hệ thống được băm bằng `BCryptPasswordEncoder`. Do đó khi dùng SQL Native để test, không được insert pass dạng plaintext mà phải hash qua code trước.
