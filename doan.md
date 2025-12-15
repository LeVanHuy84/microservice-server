# Sơ đồ tổng quan
``` mermaid
flowchart TB

%% =====================
%% CLIENT
%% =====================
subgraph CLIENT["Client Layer"]
  direction LR
  Client["Client App / Web"]
end

%% =====================
%% GATEWAY
%% =====================
subgraph GATEWAY["API Gateway Layer"]
  direction LR
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
CLIENT -->|REST / WebSocket| GATEWAY
GATEWAY -->|TCP| SERVICES
SERVICES --> INFRA

```
