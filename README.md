# 🚀 TheProduct - Spring Boot PostgreSQL DB Project

A full-stack backend project built using **Spring Boot**, supporting **PostgreSQL + MongoDB (Dual DB)** with **JWT Authentication**, **OAuth2 (Google/GitHub)**, and **GraphQL**.

---

## 🧰 Tech Stack

- Java 21
- Spring Boot 4.x
- Spring Security + JWT
- OAuth2 (Google, GitHub)
- PostgreSQL (Primary DB)
- Spring Data JPA
- Gradle (Groovy)
- JWT Auth

---

## 📦 Project Setup

### 🔹 Step 1: Generate Project

Go to: https://start.spring.io/

### Configuration:

Project: Gradle - Groovy  
Language: Java  
Spring Boot: 4.1.0  

Group: com.theproduct  
Artifact: theproduct  
Package: com.theproduct  
Packaging: Jar  
Java: 21  

---

## 📚 Dependencies

- Spring Web
- Spring Reactive Web
- Spring Security
- Spring OAuth2 Client
- Spring OAuth2 Resource Server
- Spring Data JPA
- Spring Boot DevTools
- Spring Boot Actuator
- Lombok
- PostgreSQL Driver

### 🔐 JWT Dependencies

implementation 'io.jsonwebtoken:jjwt-api:0.13.0'  
runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.13.0'  
runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.13.0'  

---

## 📁 Project Structure

src/main/java/com/theproduct/

model/ → User, Product, ProductCategory  
repository/ → HomeRepo, ProductRepo, ProductCategoryRepo, UserRepo  
service/ → HomeService, ProductService, ProductCategoryService, UserService  
controller/ → HomeController, ProductController, ProductCategoryController, UserController  

---

## ▶️ Run the Project

chmod +x gradlew  
./gradlew dependencies  
./gradlew bootRun  
./gradlew build  

---

## 🗄️ Database Setup

sudo -u postgres psql -f silversurfer_localhost-2026_07_09_07_20_39-dump.sql  

---

## 🔐 Environment Variables (.env)

GOOGLE_CLIENT_ID=your_client_id  
GOOGLE_CLIENT_SECRET=your_secret  
GITHUB_CLIENT_ID=your_client_id  
GITHUB_CLIENT_SECRET=your_secret  
JWT_SECRET=your_jwt_secret  
SPRING_SECURITY_USER_NAME=admin  
SPRING_SECURITY_USER_PASSWORD=12345  
DB_URL=jdbc:postgresql://localhost:5432/theproduct  
DB_USERNAME=postgres  
DB_PASSWORD=1234  
DATABASE_NAME=Product  

---

🔒 Security Features
JWT Authentication
OAuth2 Login (Google, GitHub)
Spring Security Configuration


🌐 API Features
REST APIs
GraphQL APIs
Image Upload & Fetch
PostgreSQL Database Support


## ⚠️ Important

- Do NOT commit `.env`
- Always use environment variables
- Regenerate secrets if exposed


📌 Future Improvements
Docker support
CI/CD pipeline
Frontend integration (React + GraphQL)
Kubernetes deployment

---

# 🚀 Done!

If you want, I can also:

✅ : POSTMAN : https://documenter.getpostman.com/view/1306351/2sBY4Jy3oH

Just tell me 👍
