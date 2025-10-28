# ARCHITECTURE.md

## 1. Sơ đồ Kiến trúc Tổng Quan

```mermaid
flowchart LR
    %% ========== CLIENT APPS ==========
    subgraph Client["📱 Client Applications"]
        direction TB
        RiderApp["🚗 Rider App"]
        DriverApp["🧭 Driver App"]
    end

    %% ========== SERVICES LAYER ==========
    subgraph Services["🧱 Microservices Layer"]
        direction TB
        US["👤 User Service<br/>(Register / Login / Profile)<br/>🗃️ PostgreSQL"]
        TS["🧾 Trip Service<br/>(Create / Manage / Cancel Trips)<br/>🗃️ PostgreSQL / MongoDB"]
        DS["🚘 Driver Service<br/>(Update Location / Availability)<br/>⚡ Redis Geo / DynamoDB"]
    end

    %% ========== DATABASES ==========
    subgraph DB["🗄️ Databases"]
        direction TB
        PSQL["🐘 PostgreSQL"]
        REDIS["🧠 Redis / 🧭 DynamoDB"]
    end

    %% ========== CLIENT ↔ SERVICES ==========
    RiderApp -->|"POST /users, /sessions"| US
    DriverApp -->|"POST /users, /sessions"| US

    RiderApp -->|"POST /trips"| TS
    RiderApp <-->|"GET /trips/{id}"| TS
    RiderApp <-->|"Trip updates (polling or WebSocket)"| TS

    DriverApp -->|"PUT /drivers/{id}/location"| DS

    %% ========== SERVICES ↔ SERVICES ==========
    TS -->|"GET /drivers/search"| DS

    %% ========== SERVICES ↔ DATABASES ==========
    US --> PSQL
    TS --> PSQL
    DS --> REDIS

```

### 🔍 Mô tả:

* **UserService**: Quản lý thông tin người dùng và xác thực.
* **TripService**: Xử lý logic đặt chuyến, tìm tài xế, cập nhật trạng thái chuyến.
* **DriverService**: Quản lý vị trí và trạng thái của tài xế, phục vụ tìm kiếm tài xế gần.
* **Redis Geo**: Lưu vị trí tài xế theo tọa độ, cho phép truy vấn tài xế gần trong bán kính rất nhanh.
* **Message Queue**: Dùng để giao tiếp bất đồng bộ giữa các service, giúp hệ thống chịu được tải cao.

Hệ thống tuân thủ nguyên tắc **Database per Service**, giúp đảm bảo tính độc lập, dễ mở rộng và giảm rủi ro khi một service gặp sự cố.

---

## 2. Sơ đồ Chi tiết cho Module A - Scalability & Performance

```mermaid
flowchart TB
    subgraph Client["📱 Client Layer"]
        RiderApp["Rider App<br/>(Realtime via WebSocket)"]
        DriverApp["Driver App<br/>(Realtime via WebSocket)"]
    end

    subgraph Gateway["🚪 API Gateway / Load Balancer"]
        GW["API Gateway<br/>(Auth, Routing, Rate Limit)"]
    end

    subgraph Async["🕓 Event Streaming Layer"]
        MQ1["SQS / Kafka<br/>(Trip Requests)"]
        MQ2["SQS / Kafka<br/>(Location Updates)"]
    end

    subgraph Core["🧩 Core Services"]
        US["UserService<br/>(PostgreSQL + Read Replica)"]
        TS["TripService<br/>Handles Trips<br/>Async via MQ1"]
        DS["DriverService<br/>Realtime Location<br/>Async via MQ2<br/>(ElastiCache Redis Geo)"]
        NS["NotificationService<br/>WebSocket / Push Notification"]
    end

    subgraph Infra["☁️ Infrastructure Layer"]
        Cache["ElastiCache / Redis Cluster<br/>(Caching, Distributed Lock)"]
        DB["PostgreSQL Cluster<br/>(Read/Write Split)"]
        AutoScale["Auto Scaling Group<br/>(ECS/K8s)"]
        Monitoring["Monitoring & Load Testing<br/>(k6 / JMeter + Grafana)"]
    end

    RiderApp --> GW
    DriverApp --> GW
    GW --> US
    GW --> TS
    GW --> DS

    TS --> MQ1
    MQ1 --> DS
    DS --> MQ2
    MQ2 --> TS

    TS --> NS
    NS --> RiderApp
    NS --> DriverApp

    US --> DB
    TS --> DB
    DS --> Cache

    TS -.-> Cache
    DS -.-> AutoScale
    TS -.-> AutoScale
    US -.-> AutoScale
    AutoScale -.-> Monitoring
```

### ⚙️ Mô tả:

* **TripService** nhận yêu cầu đặt xe, đẩy event sang **Redis Stream/SQS** để xử lý bất đồng bộ → tránh nghẽn khi có lượng lớn request đồng thời.
* **DriverService** tiêu thụ event từ hàng đợi, cập nhật vị trí tài xế vào **Redis Geo** → hỗ trợ tìm kiếm tài xế gần với độ trễ thấp (<10ms).
* Dữ liệu vị trí được ghi vào Redis trước (in-memory) và được **batch đồng bộ** về **PostgreSQL** định kỳ để lưu lâu dài.
* **Cache Layer** giúp giảm tải cho CSDL khi truy vấn thông tin tài xế hoặc kết quả tìm kiếm gần đây.
* Tích hợp **Prometheus** và **Jaeger** để quan sát hiệu năng (metrics & tracing) phục vụ kiểm chứng load testing.

### 🚀 Mục tiêu thiết kế:

* Đảm bảo hệ thống chịu tải tốt khi lượng đặt xe tăng đột biến (spike load).
* Giảm độ trễ tìm tài xế trung bình xuống < 200ms (p95 latency).
* Hỗ trợ mở rộng ngang (horizontal scaling) với DriverService và TripService độc lập.

---

## 3. Data Schema
### 3.1 User-service schema
``` mermaid
flowchart LR
    %% ===== USER SERVICE =====
    subgraph UserService["User Service"]
        class UserService userService

        USER["USER<br>———<br>id : uuid (PK)<br>email : string (unique)<br>full_name : string<br>password : string<br>role : enum(UserRole) [default: passenger]<br>phone : string (nullable)<br>created_at : timestamp [default: now()]<br>updated_at : timestamp [on update]"]

        DRIVER_PROFILE["DRIVER_PROFILE<br>———<br>id : uuid (PK)<br>user_id : uuid (FK → USER.id, unique)<br>license_number : string<br>vehicle_type : enum(VehicleType)<br>vehicle_brand : string<br>vehicle_model : string<br>license_plate : string<br>created_at : timestamp [default: now()]<br>updated_at : timestamp [on update]"]

        USER --- DRIVER_PROFILE
        %% Relationship
        USER -->|"1 — 1 (driverProfile)"| DRIVER_PROFILE
    end

```
### 3.2. Trip-service schema
``` mermaid
flowchart LR
    %% ===== TRIP SERVICE =====
    subgraph TripService["Trip Service"]
        class TripService tripService

        TRIP["TRIP<br>———<br>id : uuid (PK)<br>passenger_id : uuid (FK → USER.id)<br>driver_id : uuid (FK → USER.id, nullable)<br>vehicle_type : enum(VehicleType) [default: MOTORBIKE]<br>origin_lat : float<br>origin_lng : float<br>destination_lat : float<br>destination_lng : float<br>estimated_fare : decimal(10,2)<br>status : enum(TripStatus) [default: SEARCHING]<br>created_at : timestamp [default: now()]<br>updated_at : timestamp [on update]"]

        TRIP_RATING["TRIP_RATING<br>———<br>id : uuid (PK)<br>trip_id : uuid (FK → TRIP.id)<br>passenger_id : uuid (FK → USER.id)<br>driver_id : uuid (FK → USER.id)<br>rating : int (1–5)<br>feedback : string (nullable)<br>created_at : timestamp [default: now()]"]

        TRIP --- TRIP_RATING
        %% Relationships
        TRIP -->|"1 — n (rated)"| TRIP_RATING
    end
```

> **Tóm lại:**
>
> * Phần 1 thể hiện kiến trúc tổng thể của hệ thống UIT-Go (3 service + DB + Redis + Queue).
> * Phần 2 mô tả chi tiết module chuyên sâu (Scalability & Performance) với luồng *Find Driver / Update Location*, tập trung vào khả năng mở rộng và hiệu năng truy vấn.
