# Notes App - Spring Boot Backend

A simple and robust backend for a notes application built with Spring Boot. It provides user authentication using JWT and full CRUD functionality for notes, with data stored in MongoDB Atlas.

---

## ✨ Features

-   **User Authentication**: Secure user registration and login.
-   **JWT Security**: API endpoints are secured using JSON Web Tokens.
-   **Persistent Login**: JWT tokens are valid for 30 days for a seamless user experience.
-   **CRUD Operations**: Full Create, Read, Update, and Delete functionality for notes.
-   **Database**: Uses MongoDB Atlas for scalable and reliable data storage.

---

## 🛠️ Technologies Used

-   **Framework**: Spring Boot
-   **Language**: Kotlin
-   **Security**: Spring Security, JWT
-   **Database**: MongoDB Atlas
-   **Build Tool**: Gradle

---

## 🚀 Getting Started

### Prerequisites

-   Java 17 or later
-   Gradle
-   A MongoDB Atlas account and a connection URI

### Installation & Setup

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/AvinashKhichar/SpringBoot_NotesApp_Backend.git
    cd SpringBoot_NotesApp_Backend
    ```

2.  **Configure the application:**
    Open `src/main/resources/application.properties` and update the following properties with your own values:

    ```properties
    # MongoDB Atlas Connection URI
    spring.data.mongodb.uri=mongodb+srv://<username>:<password>@cluster-url/your-database-name?retryWrites=true&w=majority

    # JWT Configuration
    jwt.secret=your-super-secret-key-that-is-at-least-256-bits-long
    jwt.expiration.ms=2592000000
    ```

3.  **Build and run the application:**
    ```bash
    # Using Gradle
    ./gradlew bootRun
    ```

The application will start on `http://localhost:8080`.

---

## 📝 API Endpoints

Here are the primary API endpoints available:

### Authentication

-   `POST /api/auth/register` - Register a new user.
-   `POST /api/auth/login` - Login and receive a JWT.

### Notes

-   `GET /api/notes` - Get all notes for the authenticated user.
-   `POST /api/notes` - Create a new note.
-   `GET /api/notes/{id}` - Get a specific note by its ID.
-   `PUT /api/notes/{id}` - Update an existing note.
-   `DELETE /api/notes/{id}` - Delete a note.

**Note**: All `/api/notes` endpoints require a valid JWT in the `Authorization` header as a Bearer token.
