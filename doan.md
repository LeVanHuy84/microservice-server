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
%% SERVICES
%% =====================
subgraph SERVICES["Services"]
  direction LR

  ServicesCore["Services Layer"]

  User["User Service"]
  Social["Social Service"]
  Group["Group Service"]
  Post["Post Service"]
  Media["Media Service"]
  Feed["Feed Service"]
  Notify["Notification Service"]
  Chat["Chat Service"]
  Search["Search Service"]

  ServicesCore --- User
  ServicesCore --- Social
  ServicesCore --- Group
  ServicesCore --- Post
  ServicesCore --- Media
  ServicesCore --- Feed
  ServicesCore --- Notify
  ServicesCore --- Chat
  ServicesCore --- Search
end

%% =====================
%% INFRASTRUCTURE
%% =====================
subgraph INFRA["Infrastructure"]
  direction LR

  InfraCore["Infrastructure Layer"]

  PostgreSQL["PostgreSQL"]
  MongoDB["MongoDB"]
  Neo4j["Neo4j"]
  Redis["Redis"]
  Elastic["Elasticsearch"]
  RabbitMQ["RabbitMQ"]
  Kafka["Kafka"]
  Cloudinary["Cloudinary"]

  InfraCore --- PostgreSQL
  InfraCore --- MongoDB
  InfraCore --- Neo4j
  InfraCore --- Redis
  InfraCore --- Elastic
  InfraCore --- RabbitMQ
  InfraCore --- Kafka
  InfraCore --- Cloudinary
end

%% =====================
%% CONNECTIONS (OVERVIEW)
%% =====================
Client -->|REST / WebSocket| APIGW
APIGW --> ServicesCore
ServicesCore --> InfraCore

```
