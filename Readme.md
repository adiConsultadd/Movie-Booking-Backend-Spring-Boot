# Movie Booking System Backend - Spring Boot

## Introduction
This is a Spring Boot-based Movie Booking Backend with JWT authentication, MySQL database integration, and role-based access control.

## Technologies Used
- Java 17
- Spring Boot 3.2.x
- Spring Data JPA
- Spring Security (JWT Authentication)
- MySQL
- Maven
- Lombok

## Project Setup

### 1. Clone the Repository
```bash
git clone https://github.com/adiConsultadd/Movie-Booking-Backend-Spring-Boot.git
```

### 2. Create Database in MySQL
```sql
CREATE DATABASE moviebooking;
```

### 3. Configure Database Connection
Edit `src/main/resources/application.properties` and set your MySQL credentials:
```properties
server.port = 8080
spring.datasource.url=jdbc:mysql://localhost:3306/moviebooking
spring.datasource.username=yourUsername
spring.datasource.password=yourPassword
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true

# JWT Configuration
jwt.secret=yourSecretKeyHereMakeItLongAndComplexForSecurity
jwt.expiration=86400000
```

### 4. Build and Run the Application
```bash
./mvnw clean install
./mvnw spring-boot:run
```

### 5. Or Run The JAR File
```bash
cd target
java -jar movie-booking-backend.jar
```
The application will start on `http://localhost:8080`

### 6. Docker Build and Run Commands

Build the application with Maven first:
```bash
./mvnw clean package
```

Build and run using Docker Compose:
```bash
docker-compose up --build
```

Run in detached mode:
```bash
docker-compose up -d
```

Stop the containers:
```bash
docker-compose down
```

## Running Tests
To run tests, use:
```bash
./mvnw test
```

## Folder Structure
```
src/main/java/com/example/moviebooking/
├── config/             # Configuration classes
│   ├── SecurityConfig.java
│   └── JwtConfig.java
├── controllers/        # REST controllers
│   ├── AuthController.java
│   ├── AdminMovieController.java
│   ├── AdminBookingController.java
│   ├── UserMovieController.java
│   └── UserBookingController.java
├── models/             # Entity classes
│   ├── User.java
│   ├── Role.java
│   ├── Movie.java
│   ├── Showtime.java
│   └── Booking.java  
├── repositories/       # JPA repositories
│   ├── UserRepository.java
│   ├── RoleRepository.java
│   ├── MovieRepository.java
│   ├── ShowtimeRepository.java
│   └── BookingRepository.java
├── services/           # Business logic
│   ├── UserService.java
│   ├── MovieService.java
│   ├── BookingService.java
│   └── AuthService.java
├── security/           # Security-related classes
│   ├── JwtTokenProvider.java
│   ├── JwtTokenFilter.java
│   └── UserDetailsServiceImpl.java
├── dto/                # Data Transfer Objects
│   ├── UserDto.java
│   ├── LoginRequest.java
│   ├── SignupRequest.java
│   ├── JwtResponse.java
│   ├── MovieDto.java
│   └── BookingDto.java
├── exceptions/         # Custom exceptions
│   ├── ResourceNotFoundException.java
│   ├── UnauthorizedException.java
│   └── GlobalExceptionHandler.java
└── MovieBookingApplication.java  # Main class
```


## API Endpoints

### Authentication
- **User Registration**
```bash
  POST http://localhost:8080/api/auth/signup 
  {
    "username":"admin", 
    "email":"admin@example.com", 
    "password":"password123", 
    "isAdmin":true
  }'
```
- **User Login** (Retrieves JWT token)
```bash
  POST http://localhost:8080/api/auth/signin 
  {
    "username":"admin",
    "password":"password123"
  }
```

### Movie Management (Admin Only)
- **Add a New Movie**
```bash
  POST http://localhost:8080/api/admin/movies 
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
  {
    "title":"Inception", 
    "description":"A sci-fi thriller.", 
    "genre":"Sci-Fi", "durationMinutes":148, 
    "director":"Christopher Nolan", 
    "releaseDate":"2010-07-16", 
    "posterUrl":"https://example.com/inception.jpg"
  }
```
- **Update a Movie**
```bash
  PUT http://localhost:8080/api/admin/movies/movie_id
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
  {
    "title":"Inception", 
    "description":"A sci-fi thriller.", 
    "genre":"Sci-Fi", 
    "durationMinutes":148, 
    "director":"Christopher Nolan", 
    "releaseDate":"2010-07-16", 
    "posterUrl":"https://example.com/inception.jpg"
  }
```
- **Delete a Movie**
```bash
  DELETE http://localhost:8080/api/admin/movies/movie_id
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```
- **Get All Movies**
```bash
  GET http://localhost:8080/api/admin/movies/movie_id
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```
- **View All Bookings**
```bash
  GET http://localhost:8000/api/admin/bookings
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```
- **Add Showtime To A Movie**
```bash
  POST http://localhost:8000/api/admin/movies/movie_id/showtimes
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
  {
    "startTime" : "2025-03-20T14:30:00", 
    "screenNumber" :  "10", 
    "price" : 100, 
    "availableSeats" : 300,
    "totalSeats" : 300
  }
```

### User Booking
- **Get All Movies**
```bash
  GET http://localhost:8000/api/movies
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

- **Get A Specific Movie**
```bash
  GET http://localhost:8000/api/movies/movie_id
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

- **Get All Showtimes Of A Movie**
```bash
  GET http://localhost:8000/api/movies/movie_id/showtimes
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

- **Get All Bookings Of A User**
```bash
  GET http://localhost:8000/api/movies/history
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

- **Book a Movie Ticket**
```bash
  POST http://localhost:8000/api/movies/book
  -H "Authorization: Bearer YOUR_JWT_TOKEN" 
  {
    "showtimeId" : 2,
    "numberOfSeats" : 85
  }
```

- **Cancel A Booking**
```bash
  DELETE http://localhost:8000/api/movies/movie_id/cancel
  -H "Authorization: Bearer YOUR_JWT_TOKEN" 
```

