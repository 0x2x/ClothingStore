
# eCommerce Store – Clothing Store

## 📌 Project Overview

The **eCommerce Store – Clothing Store** is a backend application built with **Spring Boot** that provides RESTful APIs for managing a clothing-based eCommerce platform. The project focuses on authentication, authorization, and core store functionality, including **user login** and **admin login** using direct APIs.

This project is designed to support frontend applications (web or mobile) by exposing secure and scalable APIs.

---

## 🛠️ Tech Stack

* **Java**
* **Spring Boot**
* **Spring Web (REST APIs)**
* **Spring Security** (authentication & authorization)
* **JPA / Hibernate**
* **Database**: MySQL

---

## ✨ Features

* User authentication (Login API)
* Admin authentication (Admin Login API)
* Role-based access (User vs Admin)
* RESTful API architecture
* Secure password handling
* Scalable backend structure

---

## 🔐 Authentication APIs

### User Login

```http
POST /api/login
```

**Request Body:**

```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

---

### Admin Login

```http
POST /api/login
```

**Request Body:**

```json
{
  "email": "admin@example.com",
  "password": "adminPassword"
}
```

---

## 🚀 Getting Started

### Prerequisites

* Java 17+ (or compatible version)
* Maven
* Database (MySQL, PostgreSQL, or H2)

### Installation

1. Clone the repository:

```bash
git clone https://github.com/0x2x/ClothingStore
```

2. Navigate to the project directory:

```bash
cd ecommerce-store
```

3. Configure the database in `application.properties` or `application.yml`.

4. Build and run the application:

```bash
mvn clean install
mvn spring-boot:run
```

The server will start at:

```text
http://localhost:8080
```

---

## 🧪 Testing APIs

You can test the APIs using:

* Insomnia
* Curl

---

## 📂 Project Structure

```
src/main/java
 ├── configurations
 ├── controllers
 ├── data
 ├── models
 └── security
```

---

## 🔮 Future Enhancements

* Product management (CRUD)
* Shopping cart functionality
* Order management
* Payment gateway integration
* JWT-based authentication
* Swagger API documentation

---

## 👤 Author

**Nigel**
Backend Developer | Spring Boot

---


# Sending Requests
## Using authenitcated requests
Using authenitcated requests while not being an admin returns an error:
![img.png](img.png)

## Using non-authenticated requests
