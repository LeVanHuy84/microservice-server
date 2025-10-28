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
flowchart TB
    subgraph USER
        U1["id (uuid, PK)"]
        U2["email (string, unique)"]
        U3["full_name (string)"]
        U4["password (string)"]
        U5["role (enum: passenger | driver)"]
        U6["phone (string, optional)"]
        U7["created_at (timestamp, default now)"]
        U8["updated_at (timestamp, auto)"]
    end

    subgraph DRIVER_PROFILE
        D1["id (uuid, PK)"]
        D2["user_id (uuid, FK → USER.id, unique)"]
        D3["license_number (string)"]
        D4["vehicle_type (enum: MOTORBIKE | CAR_4_SEATS | CAR_7_SEATS)"]
        D5["vehicle_brand (string)"]
        D6["vehicle_model (string)"]
        D7["license_plate (string)"]
        D8["created_at (timestamp, default now)"]
        D9["updated_at (timestamp, auto)"]
    end

    USER -->|"1:1"| DRIVER_PROFILE
```
### 3.2. Trip-service schema
``` mermaid
flowchart LR
    subgraph TRIP["TRIP"]
        T1[id: uuid (PK)]
        T2[passenger_id: uuid]
        T3[driver_id: uuid?]
        T4[vehicle_type: enum(VehicleType: MOTORBIKE|CAR_4_SEATS|CAR_7_SEATS)]
        T5[origin_lat: float]
        T6[origin_lng: float]
        T7[destination_lat: float]
        T8[destination_lng: float]
        T9[estimated_fare: decimal]
        T10[status: enum(TripStatus)]
        T11[created_at: timestamp]
        T12[updated_at: timestamp]
    end

    subgraph TRIP_RATING["TRIP_RATING"]
        R1[id: uuid (PK)]
        R2[trip_id: uuid (FK → TRIP.id)]
        R3[driver_id: uuid]
        R4[passenger_id: uuid]
        R5[rating: int (1–5)]
        R6[feedback: string?]
        R7[created_at: timestamp]
    end

    TRIP -->|"1:1"| TRIP_RATING
```

> **Tóm lại:**
>
> * Phần 1 thể hiện kiến trúc tổng thể của hệ thống UIT-Go (3 service + DB + Redis + Queue).
> * Phần 2 mô tả chi tiết module chuyên sâu (Scalability & Performance) với luồng *Find Driver / Update Location*, tập trung vào khả năng mở rộng và hiệu năng truy vấn.
