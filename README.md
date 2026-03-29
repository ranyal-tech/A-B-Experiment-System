# 📦 A-B-Experiment-System

This project implements a simple A/B experiment system that assigns users to variants and ensures consistent results using caching.

---

## ✨ Features

- Assign users to variants based on a hash of their user ID
- Cache user assignments to ensure consistent results across sessions
- Simple REST API for retrieving user assignments

---

## 🛠️ How to Build

### 1. Clone the repository

```bash
git clone https://github.com/ranyal-tech/A-B-Experiment-System


### 2. Navigate to the project directory

```bash
cd A-B-Experiment-System
### 3. Build the project using Maven

```bash
mvn clean install

 ### 4. How to Run
Using Docker:

```bash
docker-compose up --build
This will build the Docker image and start the application on port 8080.

### Usage 
GET /experiment?userId=123
Flow: 
User sends request with userId
System checks Redis cache:
If present → return stored variant
If not → assign new variant
Store result in Redis
Return assigned variant

### Project Structure
├── src/
├── Dockerfile
├── docker-compose.yml
├── README.md

### Technologies Used
- Java
- Spring Boot
- Redis
- Docker    

### Summary
This A/B experiment system provides a simple way to assign users to variants and ensures consistent results using caching. It can be easily extended to support more complex experiment logic and additional features as needed.