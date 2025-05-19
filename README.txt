Cài đặt & triển khai hệ thống:
1. Yêu cầu hệ thống

### Backend 
- Java 17 trở lên
- Maven 3.6+
- Docker & Docker Compose 
- PostgreSQL 

### Frontend 
- Flutter SDK (>=3.7.0)
- Dart SDK (>=3.7.0)
- Android Studio/Xcode 
- Chrome 

---

## 2. Cài đặt & chạy Backend

### Cách 1: Chạy bằng Docker Compose (Khuyến nghị)

1. Mở terminal, di chuyển vào thư mục `tm-backend`:
   ```bash
   cd tm-backend
   ```

2. Chạy lệnh sau để build và khởi động backend cùng database:
   ```bash
   docker-compose up --build
   ```

3. Backend sẽ chạy tại `http://localhost:8080`, database PostgreSQL tại `localhost:5432` (user: `postgres`, pass: `postgres`).

4. Truy cập PgAdmin tại `http://localhost:5050` (user: `admin@techmart.com`, pass: `admin`).

### Cách 2: Chạy thủ công bằng Maven

1. Cài đặt PostgreSQL, tạo database tên `techmart` (user/pass: `postgres`).

2. Cấu hình file `application.properties` nếu cần.

3. Build và chạy backend:
   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```
   Hoặc:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

---

## 3. Cài đặt & chạy Frontend


1. Di chuyển vào thư mục frontend:
   ```bash
   cd tech-mart-frontend
   ```

2. Cài dependencies:
   ```bash
   flutter pub get
   ```

3. Chạy ứng dụng:
   - **Web:** 
     ```bash
     flutter run -d chrome
     ```
   - **Android/iOS:** 
     Kết nối thiết bị/simulator rồi chạy:
     ```bash
     flutter run
     ```

---

## 4. Một số lưu ý

- API backend mặc định chạy ở `http://localhost:8080`
- Tài khoản đăng nhập:
  + Quyền admin: username: admminn, passwword: 121212
  + Quyền customer: username: khuongg, password:121212


- link github: 
  + https://github.com/khuong2924/tech-mart-frontend
  + https://github.com/khuong2924/tech-mart

- link video: https://www.youtube.com/watch?v=tcEWkfEQ_go