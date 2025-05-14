<h1 align="center">Pioner Pixel 💸</h1>
<p align="center">
  Тестовое задание powered by <b>Spring Boot 3</b> &nbsp;|&nbsp; JWT&nbsp;Auth + Redis&nbsp;Cache + Testcontainers
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-blue?logo=openjdk" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.2.0-brightgreen?logo=springboot" />
  <img src="https://img.shields.io/badge/PostgreSQL-%3E%3D14-blue?logo=postgresql" />
  <img src="https://img.shields.io/badge/Redis-%3E%3D6-red?logo=redis" />
  <img src="https://img.shields.io/github/actions/workflow/status/your-org/pioner-pixel/ci.yml?logo=github" />
</p>



## Features

- **JWT authentication** (cookie + `Authorization: Bearer`)  
- **Spring Cache** backed by Redis, TTL 10 min  
- **Flyway** migrations with demo data  
- **Money transfers** (`/transfers`) with SERIALIZABLE isolation  
- **Audit log** – every transfer ends up in the log as REQUEST / SUCCESS / ERROR  
- **Balance scheduler** – increases every account by 10 % each 30 s, capped at ×2.07 of initial deposit  
- REST docs via **Springdoc / Swagger-UI**  
- Integration tests on **real Postgres** thanks to Testcontainers

---

## Quick start

```bash
# 0 — prerequisites
#  ▸ JDK 21+    ▸ Maven 3.9+    

# 1. configure connection strings
cp .env.sample .env            # edit DB / Redis / SSL

# 2. build
mvn clean package 

# 2.1 setup and run Postgres/Redis locally

# 3. run
java -jar target/pioner-pixel-*.jar
# ➜  API:   http://localhost:8080/v3/api-docs | 8443 port with ssl
# ➜  UI :   http://localhost:8080/      (login page) | 8443 port with ssl

