# Tài Liệu Nghiệp Vụ (Business Document)
**Dự án**: Employee Management System (Hệ thống Quản lý Nhân sự)

---

## 1. Tổng quan hệ thống
Hệ thống Quản lý Nhân sự là một nền tảng quản trị nội bộ giúp doanh nghiệp số hóa quy trình quản lý nhân viên, phòng ban và chức vụ. Hệ thống được thiết kế để phân tách rõ ràng giữa khái niệm "Tài khoản đăng nhập" (User) và "Hồ sơ nhân sự" (Employee), từ đó linh hoạt trong việc cấp phát quyền truy cập và lưu trữ thông tin nghiệp vụ.

## 2. Phân Quyền Hệ Thống (RBAC - Role Based Access Control)
Hệ thống sử dụng cơ chế phân quyền dựa trên Role với 2 vai trò chính:
- **`ROLE_ADMIN` (Quản trị viên)**: Nắm toàn quyền hệ thống. Có thể tạo mới nhân viên, thiết lập phòng ban, thay đổi chức vụ, và xem toàn bộ hồ sơ của mọi nhân viên.
- **`ROLE_USER` (Nhân viên/Người dùng tiêu chuẩn)**: Chỉ có thể xem và cập nhật thông tin cá nhân (Profile) của chính mình. Không có quyền can thiệp vào các thiết lập cấp công ty (Phòng ban, Lương, Hồ sơ của người khác).

## 3. Các Thực Thể Nghiệp Vụ Cốt Lõi (Core Entities)

### 3.1. Tài khoản & Xác thực (User & Authentication)
- **User**: Mọi cá nhân muốn truy cập vào hệ thống đều phải có tài khoản. Tài khoản chứa các thông tin cơ bản: Username, Password, Email, Tên, Giới tính.
- **Bảo mật & Quên mật khẩu**:
  - Khi người dùng muốn đổi mật khẩu, bắt buộc phải xác thực bằng mật khẩu cũ (Old Password).
  - Nếu quên mật khẩu, hệ thống cung cấp luồng cấp lại thông qua mã OTP gửi qua Email. Người dùng nhập OTP hợp lệ sẽ được đổi mật khẩu mới mà không cần nhớ mật khẩu cũ.

### 3.2. Sơ đồ tổ chức công ty (Department & Position)
- **Phòng ban (Department)**: Quản lý các khối/phòng ban trong công ty (Ví dụ: Khối Kỹ Thuật, Khối Nhân Sự). Chứa thông tin về tên, mô tả và vị trí làm việc.
- **Chức vụ (Position)**: Mỗi phòng ban sẽ có nhiều chức vụ khác nhau (Ví dụ: Tech Lead, Junior Developer thuộc khối Kỹ Thuật). Mỗi chức vụ sẽ có một mức **Lương cơ bản (Base Salary)** mặc định.

### 3.3. Hồ sơ nhân sự (Employee)
- **Employee** là bản ghi gắn kết giữa một "Tài khoản" (User) và một "Chức vụ" (Position) tại công ty.
- Nó chứa các thông tin nghiệp vụ độc lập mà một tài khoản thông thường không có:
  - **Mã nhân viên (Employee Code)**: Định danh nội bộ (Ví dụ: `EMP-2026-00001`).
  - **Ngày vào làm (Hire Date)**.
  - **Trạng thái làm việc (Status)**: Đang làm việc (ACTIVE), Nghỉ phép (ON_LEAVE), Đã nghỉ việc (TERMINATED)...
- *Lưu ý*: Một User chỉ có thể sở hữu 1 hồ sơ Employee duy nhất. 

## 4. Các Luồng Nghiệp Vụ Chính (Main Flows)

> [!IMPORTANT]
> **Luồng Tuyển dụng & Cấp phát tài khoản**
> 1. Admin tạo **Phòng ban** và các **Chức vụ** tương ứng.
> 2. Người mới được cấp một **User** (Hoặc họ tự Register và chờ duyệt).
> 3. Admin tạo hồ sơ **Employee**, gắn User đó vào một Position cụ thể. Từ lúc này, User chính thức trở thành nhân viên công ty.

> [!NOTE]
> **Luồng Báo cáo thống kê**
> Hệ thống có khả năng xuất các báo cáo nhanh (Ví dụ: Có bao nhiêu nhân sự ở từng phòng ban, bao nhiêu người đang Active, quỹ lương cơ bản của phòng ban đó là bao nhiêu) thông qua các Stored Procedure được tính toán sâu bên trong Database.

## 5. Quy định về Dữ Liệu
- **Soft Delete / Hard Delete**: Hệ thống ưu tiên việc đổi `EmployeeStatus` thành `TERMINATED` (Nghỉ việc) thay vì xóa thẳng (Hard Delete) dữ liệu khỏi Database để đảm bảo lịch sử hệ thống. Các thao tác xóa (Delete) chỉ thực hiện khi dữ liệu nhập sai hoàn toàn.
- **Tính Duy Nhất**: Email, Username và Mã Nhân Viên (Employee Code) là định danh duy nhất trên toàn hệ thống, không được phép trùng lặp.
