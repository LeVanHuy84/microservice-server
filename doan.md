# Sơ đồ tổng quan
``` mermaid
flowchart TB

%% =====================
%% CLIENT
%% =====================
subgraph CLIENT["Client Layer"]
  Client["Client App / Web"]
end

%% =====================
%% GATEWAY
%% =====================
subgraph GATEWAY["API Gateway Layer"]
  APIGW["API Gateway Node"]
end

%% =====================
%% CORE SERVICES
%% =====================
subgraph SERVICES["Core Services"]
  direction LR

  User["User Service"]
  Social["Social Service"]
  Group["Group Service"]
  Post["Post Service"]

  Media["Media Service"]
  Feed["Feed Service"]
  Notify["Notification Service"]
  Message["Messaging Service"]
end

%% =====================
%% INFRASTRUCTURE
%% =====================
subgraph INFRA["Infrastructure"]
  direction LR

  PostgreSQL["PostgreSQL"]
  MongoDB["MongoDB"]
  Neo4j["Neo4j"]

  Redis["Redis"]
  Elastic["Elasticsearch"]
  RabbitMQ["RabbitMQ"]

  Kafka["Kafka"]
  Cloudinary["Cloudinary"]
end

%% =====================
%% CONNECTIONS
%% =====================
Client -->|REST / WebSocket| APIGW
APIGW -->|TCP| User
APIGW -->|TCP| Post
APIGW -->|TCP| Feed

User --> PostgreSQL
Social --> MongoDB
Group --> Neo4j

Feed --> Redis
Search -.-> Elastic
Notify --> RabbitMQ
Feed --> Kafka
Media --> Cloudinary

```
