# RecipeHub

RecipeHub is an online recipe sharing platform that allows users to discover, share, save, rate, and review recipes.

The platform provides different functionality for Administrators, Recipe Contributors, and Recipe Explorers.

## Features

### User Authentication

* User registration and login
* BCrypt password hashing
* Session-based authentication
* Logout functionality
* Current-user session management

### Recipe Contributors

Contributors can:

* Add new recipes
* Edit their recipes
* Delete their recipes
* View their submitted recipes
* Add ingredients and cooking instructions
* Assign recipes to categories
* Add recipe images using image URLs

### Recipe Explorers

Explorers can:

* Browse recipes
* Search recipes
* Filter recipes by category
* View recipe details
* Read reviews and ratings
* Save recipes
* Rate recipes
* Write reviews

### Administrator

Administrators can:

* View pending recipe submissions
* Approve recipes
* Reject recipes
* Manage recipe content

### Reviews and Ratings

* Users can rate recipes
* Users can write reviews
* Average recipe ratings are calculated
* Reviews can be viewed from recipe details

### Saved Recipes

Users can:

* Save recipes
* Remove saved recipes
* View their saved recipes

## User Roles

| Role        | Description                                  |
| ----------- | -------------------------------------------- |
| ADMIN       | Manages and approves recipe submissions      |
| CONTRIBUTOR | Creates and manages recipes                  |
| EXPLORER    | Discovers, saves, rates, and reviews recipes |

## Technology Stack

### Frontend

* HTML5
* CSS3
* JavaScript
* Responsive UI

### Backend

* Java
* Spring Boot
* Spring Data JPA
* Hibernate
* Maven
* REST APIs

### Database

* MySQL 8

### Security

* BCrypt password hashing
* HTTP Session-based authentication
* Role-based access control

## Application Architecture

```text
Browser
   |
   | HTTP Requests
   v
Spring Boot REST API
   |
   v
Service Layer
   |
   v
Repository / JPA
   |
   v
MySQL Database
```

## Project Structure

```text
RecipeHub/
|
├── src/
│   └── main/
│       ├── java/
│       │   └── ...
│       │       ├── config/
│       │       ├── controller/
│       │       ├── model/
│       │       ├── repository/
│       │       └── service/
│       │
│       └── resources/
│           ├── static/
│           │   ├── css/
│           │   ├── *.html
│           │   └── *.js
│           │
│           └── application.properties
│
├── pom.xml
├── README.md
└── .gitignore
```

## Database

RecipeHub uses MySQL 8 as its persistent database.

The main tables are:

* users
* recipes
* reviews
* saved_recipes

### Users

Stores user information including:

* User ID
* Name
* Email
* Password
* Role

Passwords are stored using BCrypt hashing.

### Recipes

Stores:

* Recipe ID
* Title
* Description
* Ingredients
* Instructions
* Image URL
* Category
* Status
* Author
* Average rating

### Reviews

Stores:

* Rating
* Comment
* User
* Recipe

### Saved Recipes

Stores the relationship between users and their saved recipes.

## Getting Started

### Prerequisites

Install the following:

* Java 17 or compatible Java version
* Maven
* MySQL 8
* Git

### Clone the Repository

```bash
git clone https://github.com/Parth-verse/RecipeHub.git
cd RecipeHub
```

### Configure MySQL

Make sure MySQL Server is running.

The application uses:

```text
Host: localhost
Port: 3306
Database: recipehub
Username: root
```

The database can be created automatically through the application's MySQL configuration.

### Configure the Database Password

The MySQL password is not stored in the repository.

Set the password as an environment variable.

For Windows PowerShell:

```powershell
$env:MYSQL_PASSWORD="YOUR_MYSQL_PASSWORD"
```

Replace the placeholder with your local MySQL password.

Do not commit your password to GitHub.

### Run the Application

```bash
mvn spring-boot:run
```

The application runs on:

```text
http://localhost:8085
```

Open the URL in a browser after the application starts.

## Development Test Accounts

The project includes seeded accounts for testing the different roles.

| Role        | Email                                             | Password |
| ----------- | ------------------------------------------------- | -------- |
| ADMIN       | [admin@recipehub.com](mailto:admin@recipehub.com) | admin123 |
| CONTRIBUTOR | [chef@recipehub.com](mailto:chef@recipehub.com)   | chef123  |
| EXPLORER    | [alice@recipehub.com](mailto:alice@recipehub.com) | alice123 |

These accounts are intended only for development and demonstration.

## REST API

### Authentication

| Method | Endpoint             | Description         |
| ------ | -------------------- | ------------------- |
| POST   | `/api/auth/register` | Register a new user |
| POST   | `/api/auth/login`    | Login               |
| POST   | `/api/auth/logout`   | Logout              |
| GET    | `/api/auth/current`  | Get current user    |

### Recipes

| Method | Endpoint            | Description               |
| ------ | ------------------- | ------------------------- |
| GET    | `/api/recipes`      | Get recipes               |
| GET    | `/api/recipes/{id}` | Get recipe details        |
| GET    | `/api/recipes/my`   | Get contributor's recipes |
| POST   | `/api/recipes`      | Create recipe             |
| PUT    | `/api/recipes/{id}` | Update recipe             |
| DELETE | `/api/recipes/{id}` | Delete recipe             |

### Administration

| Method | Endpoint                          | Description          |
| ------ | --------------------------------- | -------------------- |
| GET    | `/api/admin/recipes/pending`      | View pending recipes |
| PUT    | `/api/admin/recipes/{id}/approve` | Approve recipe       |
| PUT    | `/api/admin/recipes/{id}/reject`  | Reject recipe        |

### Reviews

| Method | Endpoint                          | Description   |
| ------ | --------------------------------- | ------------- |
| GET    | `/api/recipes/{recipeId}/reviews` | Get reviews   |
| POST   | `/api/recipes/{recipeId}/reviews` | Add review    |
| DELETE | `/api/reviews/{id}`               | Delete review |

### Saved Recipes

| Method | Endpoint                     | Description         |
| ------ | ---------------------------- | ------------------- |
| GET    | `/api/saved-recipes`         | Get saved recipes   |
| POST   | `/api/recipes/{id}/save`     | Save recipe         |
| DELETE | `/api/recipes/{id}/save`     | Remove saved recipe |
| GET    | `/api/recipes/{id}/is-saved` | Check saved status  |

## Security

The project uses the following security measures:

* BCrypt password hashing
* Environment-based database password configuration
* Git-ignored environment files
* Session-based authentication
* Role-based access control
* MySQL as the persistent database
* No database password committed to the repository

## Application Flow

```text
User
 |
 +-- Register / Login
 |
 v
Authentication
 |
 v
Role Detection
 |
 +----------------+----------------+
 |                |                |
 v                v                v
ADMIN         CONTRIBUTOR       EXPLORER
 |                |                |
 |                +-- Add Recipe   +-- Browse Recipes
 |                +-- Edit Recipe  +-- Search
 |                +-- Delete       +-- Save
 |                                 +-- Rate
 +-- Approve / Reject              +-- Review
```

## Current Status

The current version includes:

* Spring Boot backend
* MySQL database integration
* User authentication
* Role-based access
* Recipe CRUD operations
* Recipe approval system
* Search and categories
* Reviews and ratings
* Saved recipes
* Responsive frontend
* REST API
* Environment-based database configuration

## Future Improvements

Some possible improvements include:

* Cloud deployment
* Cloud image storage
* Email verification
* Password reset
* Recipe recommendations
* Pagination
* Social sharing
* Improved moderation
* Nutrition information
* Ingredient-based recipe recommendations

## License

This project was developed as an academic/college project.
