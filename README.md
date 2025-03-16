# Cryptocurrency Trading Simulator Documentation

## 1. Overview
The Cryptocurrency Trading Simulator is a web-based application that allows users to buy and sell cryptocurrencies using real-time price data fetched from the Kraken API. The system maintains user portfolios, transaction history, and account balances while ensuring security through token-based authentication.

## 2. Architecture

### Backend
- **Framework**: Spring Boot (Java)
- **Real-time Price Updates**: WebSocket integration with Kraken API

### Frontend
- Web-based application that dynamically updates prices and trading inputs.

### Database
- **Database**: PostgreSQL
- **Tables**:
  - Users
  - Transactions
  - Owned Assets

### External API
- Kraken API for real-time cryptocurrency price updates.

## 3. Authentication & Security

### JWT-based Stateless Authentication:
- **Login & Token Generation**:
  - Users log in with a username and password.
  - The backend verifies credentials and returns a JWT token.
  - The token is stored in `localStorage`.

- **Request Authorization**:
  - Every API request must include the token in the `Authorization` header.
  - The backend validates the token before processing the request.

- **Logout & Token Expiry**:
  - Logging out removes the token from storage.
  - If a token expires, users must log in again.

- **Access Control**:
  - Unauthorized users are redirected to the login page (`/`).

## 4. WebSocket Data Flow

### Backend WebSocket Connection to Kraken API:
- The server maintains a WebSocket connection with Kraken to fetch cryptocurrency prices.

### Frontend WebSocket Updates:
- The backend forwards price updates to the frontend using another WebSocket connection.
- The frontend dynamically updates the displayed prices.

## 5. Database Schema

### 5.1 Users Table
| Field       | Type   | Description                                  |
|-------------|--------|----------------------------------------------|
| `id`        | UUID   | Unique ID for the user.                      |
| `username`  | String | Unique username (3-20 characters).           |
| `passwordHash` | String | Hashed password for security.              |
| `balance`   | Double | User's current balance (≥ 0).                |

### 5.2 Transactions Table
| Field       | Type   | Description                                  |
|-------------|--------|----------------------------------------------|
| `id`        | UUID   | Unique transaction ID.                       |
| `user_id`   | UUID   | Foreign key referencing the user.            |
| `type`      | String | Either `buy` or `sell`.                      |
| `assetSymbol` | String | The cryptocurrency symbol (1-10 chars).    |
| `quantity`  | Double | Amount of the asset transacted (≥ 0).        |
| `price`     | Double | Price per unit at the time of transaction (≥ 0).|
| `total`     | Double | Total transaction value (≥ 0).               |

### 5.3 Owned Assets Table
| Field       | Type   | Description                                  |
|-------------|--------|----------------------------------------------|
| `id`        | UUID   | Unique ID of the owned asset record.         |
| `user_id`   | UUID   | Foreign key referencing the user.            |
| `assetSymbol` | String | The cryptocurrency symbol (1-10 chars).    |
| `quantity`  | Double | The number of units owned (≥ 0).             |
| `averagePrice` | Double | Average price paid per unit (≥ 0).          |

## 6. Frontend Features

- **Real-time Price Updates**: Prices dynamically update using WebSocket communication.
- **Conditional Asset Display**: If a user does not own an asset, it is hidden from the portfolio.
- **Dynamic Trading Input Fields**:
  - The price updates automatically in the buy/sell window.
  - Changing quantity or value fields auto-adjusts the other field based on the current price.
- **Real-time Profit Calculation**: 
  - The frontend calculates and displays the user's profit/loss in both absolute value ($) and percentage (%) based on the current market price, average purchase price, and quantity owned.
- **Reset Account Feature**:
  - Users can reset their account, which:
    - Clears all transactions and owned assets.
    - Resets balance to the starting value.

## 7. API Endpoints

### 7.1 Authentication Endpoints
- **POST `/register`** – Registers the user with provided credentials.
- **POST `/login`** – Authenticates the user and returns a JWT token.
- **POST `/logout`** – Invalidates the JWT token and logs out the user.

### 7.2 Transaction Endpoints
- **POST `/transactions`** – Initiates a transaction for a cryptocurrency asset.
- **GET `/transactions`** – Retrieves all transactions associated with the user.

### 7.3 User Account Management
- **POST `/reset`** – Resets user transactions, owned assets, and balance.
- **GET `/assets/{asset}`** – Returns the quantity and average price of the asset owned by the user.
- **GET `/user`** – Retrieves user data including owned assets, transactions, and balance.
- **GET `/balance`** – Retrieves the balance of the authenticated user.
- **POST `/validate-token`** – Checks if the provided JWT token is still valid.

## 8. Conclusion
This app provides a secure, real-time cryptocurrency trading experience using WebSockets for live data updates. It ensures authentication security via JWT and provides an intuitive UI for executing transactions dynamically. The app uses persistent storage with a PostgreSQL database to manage user data, transactions, and owned assets.

# Instructions on How to Run the Application

## 1. Set Up PostgreSQL Database

### Install and Run PostgreSQL
1. Download and install PostgreSQL.
2. Start the PostgreSQL service.
3. Create Database and User

## 2. Configure Application Properties

### Prepare Configuration Files
1. Rename the provided `sample_application.properties` file to `application.properties`.
2. Place `application.properties` in the `src/main/resources` directory of your project.
3. Set database connection and other required properties inside the `application.properties` file.

## 3. Set Up Environment Variables

### Prepare the Environment File
1. Rename the provided `sample.env` file to `.env`.
2. Place the `.env` file in the root directory of your project.
3. Define environment variables as required.

## 4. Run the Application

### Using an IDE
1. Open your preferred IDE (e.g., IntelliJ IDEA, Eclipse).
2. Import the Spring Boot project.
3. Run the main method of your Spring Boot application thats located in `src/main/java/com/cryptotrading/CryptoApplication.java`.

### Using Command Line
1. Open a terminal in the root directory of your project.
2. Ensure that the environment variables from the `.env` file are loaded.
3. Build and run the application using Maven or Gradle.

Your application should now be running on `localhost:8080` and connected to the PostgreSQL database!


