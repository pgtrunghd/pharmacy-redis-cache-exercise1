# Bài tập — Caching danh mục thuốc bằng Redis

Đề bài: https://portal.rikkei.edu.vn/learn/159/homework/8341

Ứng dụng Spring Boot lưu thuốc trong PostgreSQL và dùng Redis làm cache cho API lấy chi tiết. `MedicineService.getById` có `@Cacheable(value = "medicines", key = "#id")`: lần đầu đọc PostgreSQL rồi lưu kết quả vào Redis, những lần sau cùng ID đọc từ cache. Giá trị cache là byte array Java serialization của `MedicineResponse`; thời gian sống 10 phút.

## Yêu cầu

- JDK 21, PostgreSQL và Redis.
- PostgreSQL có database `pharmacy_cache_db` và tài khoản có quyền tạo bảng.
- Spring Boot 3.5.6, Gradle Wrapper 8.14.3.

Nếu macOS đang dùng JDK khác: `export JAVA_HOME=$(/usr/libexec/java_home -v 21)`.

## Chạy ứng dụng

Từ thư mục gốc repository:

```bash
DB_PASSWORD='mat-khau-postgresql-cua-ban' ./gradlew bootRun
```

Các biến tùy chọn: `DB_USERNAME` (mặc định `postgres`), `DB_NAME` (`pharmacy_cache_db`), `DB_PORT` (`5432`), `REDIS_HOST` (`localhost`), `REDIS_PORT` (`6379`), `SERVER_PORT` (`8081`). Ứng dụng tự tạo bảng `medicines` qua Hibernate trong môi trường thực hành.

## Thử cache

Tạo một thuốc mẫu:

```bash
curl -fsS -X POST http://localhost:8081/api/v1/medicines \
  -H 'Content-Type: application/json' \
  -d '{"name":"Paracetamol 500mg","price":12000}'
```

Lấy ID trả về (ví dụ `1`) và gọi hai lần:

```bash
curl -fsS http://localhost:8081/api/v1/medicines/1
curl -fsS http://localhost:8081/api/v1/medicines/1
```

Vì `spring.jpa.show-sql=true`, console hiện đúng một câu `Hibernate: select ... from medicines ...` cho hai lần GET cùng ID. Redis có khóa `medicines::1`; có thể kiểm tra bằng `redis-cli KEYS 'medicines::*'`. Trong lần kiểm thử cục bộ, lời gọi thứ hai mất 7,5 ms. Thời gian thực tế phụ thuộc máy và môi trường chạy.

## Build

```bash
./gradlew clean test bootJar
```

Mật khẩu PostgreSQL được truyền bằng biến môi trường và không nằm trong repository.
