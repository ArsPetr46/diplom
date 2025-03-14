# Web-Based Messaging System

This project is a web-based messaging system, similar to the web versions of Discord or Telegram. Currently, it only contains the backend part, which connects to a local server. In the future, the server will be hosted online. The frontend will be developed using React, and the necessary JavaScript files will be added later.

## Technologies Used

- **Java**: 23
- **Spring Boot**: 3.3.5
- **Maven**: 4.0.0

## Prerequisites

Make sure you have the following installed on your system:

- Java 23
- Maven 4.0.0

## Getting Started

1. **Clone the repository:**

    ```sh
    git clone https://github.com/ArsPetr46/diplom.git
    cd diplom
    ```

2. **Add database credentials as environmental properties in your IDE or OS. You should set these properties:**

   ```ini
    DB_URL
    DB_USERNAME
    DB_PASSWORD
    ```

3. **Build the project:**

    ```sh
    ./mvnw clean install
    ```

4. **Run the application:**

    ```sh
    ./mvnw spring-boot:run
    ```

The backend server will start and connect to the local server. Once the frontend is developed, it will be integrated into this project.