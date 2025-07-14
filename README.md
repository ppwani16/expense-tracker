
# Expense Tracker Application

A comprehensive expense tracking application built with Spring Boot backend and AngularJS frontend. The application provides intuitive expense management with powerful analytics and visualization features.

## Features

- **Expense Management**: Add, edit, delete, and view expenses
- **Filtering & Sorting**: Filter by date range, month, or recent expenses with sorting options
- **Analytics**: Monthly/yearly summaries, category-wise analysis
- **Visualizations**: Interactive charts for expense trends and category distribution
- **Responsive Design**: Professional UI that works on all devices
- **Real-time Updates**: Instant updates when expenses are added or modified

## Technology Stack

### Backend
- Java 17
- Spring Boot 3.1.0
- Spring Web
- Spring Validation
- JUnit 5
- Maven 3.8+

### Frontend
- HTML5
- CSS3
- AngularJS 1.8.2
- Chart.js
- Responsive Design

## Project Structure

```

expense-tracker/
├── backend/
│   ├── src/main/java/com/expensetracker/
│   │   ├── controller/          \# REST controllers
│   │   ├── service/             \# Business logic
│   │   ├── repository/          \# Data access layer
│   │   ├── model/               \# Domain models
│   │   ├── dto/                 \# Data transfer objects
│   │   └── config/              \# Configuration classes
│   ├── src/test/java/           \# Unit tests
│   └── pom.xml                  \# Maven dependencies
├── frontend/
│   ├── index.html               \# Main HTML file
│   ├── css/style.css            \# Styling
│   ├── js/
│   │   ├── app.js               \# AngularJS app configuration
│   │   ├── controllers/         \# AngularJS controllers
│   │   └── services/            \# AngularJS services
│   └── views/                   \# HTML templates
└── README.md

```

## Prerequisites

- Java 17 or higher
- Maven 3.8 or higher
- Modern web browser (Chrome, Firefox, Safari, Edge)

## Installation & Setup

### Backend Setup

1. Navigate to the backend directory:
```

cd expense-tracker/backend

```

2. Build the project:
```

mvn clean install

```

3. Run the application:
```

mvn spring-boot:run

```

The backend will start on `http://localhost:8080`

### Frontend Setup

1. Navigate to the frontend directory:
```

cd expense-tracker/frontend

```

2. Start a local web server. You can use any of these options:

**Option 1: Using Python (if installed)**
```


# Python 3

python -m http.server 3000

# Python 2

python -m SimpleHTTPServer 3000

```

**Option 2: Using Node.js http-server (if Node.js is installed)**
```

npm install -g http-server
http-server -p 3000

```

**Option 3: Using any other web server**
- Open the frontend folder in any local web server
- Ensure it's served on port 3000 or update the API_BASE_URL in js/app.js

3. Open your browser and navigate to `http://localhost:3000`

## Testing

### Backend Tests

Run all tests:
```

cd backend
mvn test

```

Run specific test class:
```

mvn test -Dtest=ExpenseServiceTest

```

### REST API Testing with CURL

#### Create an Expense
```

curl -X POST http://localhost:8080/api/expenses \
-H "Content-Type: application/json" \
-d '{
"description": "Grocery Shopping",
"amount": 75.50,
"category": "Food",
"date": "2025-01-15T10:30:00"
}'

```

#### Get All Expenses
```

curl -X GET http://localhost:8080/api/expenses

```

#### Get Expense by ID
```

curl -X GET http://localhost:8080/api/expenses/1

```

#### Update an Expense
```

curl -X PUT http://localhost:8080/api/expenses/1 \
-H "Content-Type: application/json" \
-d '{
"description": "Updated Grocery Shopping",
"amount": 80.00,
"category": "Food",
"date": "2025-01-15T10:30:00"
}'

```

#### Delete an Expense
```

curl -X DELETE http://localhost:8080/api/expenses/1

```

#### Get Recent Expenses
```

curl -X GET "http://localhost:8080/api/expenses/recent?limit=10"

```

#### Get Expenses by Month
```

curl -X GET http://localhost:8080/api/expenses/month/2024/1

```

#### Get Expense Summary
```

curl -X GET http://localhost:8080/api/expenses/summary

```

#### Get Expenses by Category
```

curl -X GET http://localhost:8080/api/expenses/by-category

```

#### Get Monthly Trend
```

curl -X GET http://localhost:8080/api/expenses/trend/2024

```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/expenses` | Create new expense |
| GET | `/api/expenses` | Get all expenses |
| GET | `/api/expenses/{id}` | Get expense by ID |
| PUT | `/api/expenses/{id}` | Update expense |
| DELETE | `/api/expenses/{id}` | Delete expense |
| GET | `/api/expenses/recent` | Get recent expenses |
| GET | `/api/expenses/month/{year}/{month}` | Get expenses by month |
| GET | `/api/expenses/date-range` | Get expenses by date range |
| GET | `/api/expenses/sorted` | Get sorted expenses |
| GET | `/api/expenses/summary` | Get expense summary |
| GET | `/api/expenses/by-category` | Get expenses by category |
| GET | `/api/expenses/trend/{year}` | Get monthly trend |

## UI Navigation & Features

### Main Interface
- **Header**: Displays monthly and yearly expense summaries
- **Left Panel**: Expense form and summary statistics
- **Right Panel**: Expense list with filtering and sorting options
- **Charts Section**: Visual analytics with category and trend charts

### Adding Expenses
1. Fill in the expense form on the left panel
2. All fields are required and validated
3. Press Enter in any field to clear it after submission
4. Click "Add Expense" to save

### Editing Expenses
1. Click the "Edit" button on any expense item
2. The form will populate with existing data
3. Modify the fields as needed
4. Click "Update Expense" to save changes

### Filtering & Sorting
- **Filter Options**: All, Recent 50, This Month, Date Range
- **Sort Options**: Date, Amount, Category
- **Sort Order**: Ascending/Descending toggle

### Charts & Analytics
- **Category Chart**: Doughnut chart showing expense distribution by category
- **Trend Chart**: Line chart showing monthly expense trends
- **Summary Statistics**: Total, highest/lowest categories

## Build Scripts

### Backend Build
```


# Clean and compile

mvn clean compile

# Run tests

mvn test

# Package application

mvn package

# Run application

mvn spring-boot:run

```

### Production Build
```


# Backend

cd backend
mvn clean package -DskipTests

# Frontend - minify resources (optional)

# Use build tools like Webpack or Gulp for optimization

```

## Troubleshooting

### Common Issues

1. **CORS Issues**: Ensure WebConfig is properly configured
2. **Port Conflicts**: Change server.port in application.properties
3. **Database Connection**: Application uses in-memory storage
4. **Frontend Not Loading**: Check if web server is running on correct port
5. **API Calls Failing**: Verify backend is running and API_BASE_URL is correct

