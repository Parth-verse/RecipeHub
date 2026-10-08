# RecipeHub

> A full-stack web application for discovering, sharing, and reviewing culinary recipes, designed with a clean layered architecture, role-based access control, and a responsive food-inspired user experience.

---

## Features

* **User Registration & Login**: Easy account registration with secure session-based authentication (`HttpSession`).
* **BCrypt Password Hashing**: Passwords hashed securely using Spring Security's `BCryptPasswordEncoder`.
* **Role-Based Access Control**:
  * **Admin**: Review recipe submissions, approve or reject community contributions, and manage content.
  * **Recipe Contributor**: Create, edit, and delete personal recipes; track approval status (`PENDING`, `APPROVED`, `REJECTED`).
  * **Recipe Explorer**: Discover dishes, bookmark favorite recipes, submit reviews, and rate recipes 1–5 stars.
* **Recipe Creation & Management**: Multi-field recipe submission including title, category, description, ingredients, cooking instructions, and image URL.
* **Recipe Moderation Workflow**: Contributor recipes start in `PENDING` state and require Admin approval before appearing in public feeds.
* **Search & Category Filtering**: Case-insensitive keyword title search and category-based filtering across categories (Curry, Rice, Italian, Chinese, Mexican, Dessert, Breakfast, Snacks, Beverages).
* **Reviews & 1–5 Star Ratings**: Community review system with average rating calculation and duplicate review prevention per user.
* **Saved / Bookmarked Recipes**: Personal bookmark collection with single-click save and remove functionality.
* **Responsive Frontend**: Clean food-themed UI built with Vanilla HTML5, CSS3, and modern JavaScript (Fetch API).
* **MySQL 8 Database**: Relational schema persistence with Hibernate / JPA automatic table synchronization.

---

## Tech Stack

### Frontend
* **HTML5**: Semantic structure and accessible layouts
* **CSS3**: Responsive flexbox and grid design with custom CSS variables
* **JavaScript (ES6+)**: Vanilla asynchronous API communication via Fetch API

### Backend
* **Java 17**: Core language
* **Spring Boot 3.3.4**: Framework for web MVC and RESTful APIs
* **Spring Data JPA / Hibernate 6**: Object-relational mapping and repository abstraction
* **Apache Maven**: Dependency and build lifecycle management

### Database
* **MySQL 8.0**: Relational database engine (`recipehub` database)

### Security
* **BCrypt**: One-way salt-hashed cryptographic password protection
* **HTTP Session**: Cookie-backed (`JSESSIONID`) stateful session authentication

---

## Architecture

The application implements standard layered architecture:

```text
Browser (Frontend UI)
       │  HTTP JSON Requests (Fetch API)
       ▼
Spring Boot REST API (Controller Layer)
       │  Java Method Invocations
       ▼
Service Layer (Business Logic & Validation)
       │  Entity Operations
       ▼
Repository Layer (Spring Data JPA / Hibernate)
       │  SQL Queries
       ▼
MySQL 8 Database (Relational Tables)
```

---

## Project Structure

```text
RecipeHub/
├── pom.xml                                   # Maven dependencies and configuration
├── .gitignore                                # Excludes build artifacts and environment secrets
├── README.md                                 # Project documentation
└── src/
    └── main/
        ├── java/
        │   └── com/recipehub/
        │       ├── RecipeHubApplication.java # Spring Boot entry point
        │       │
        │       ├── config/
        │       │   ├── DatabaseConfig.java   # MySQL DataSource initialization
        │       │   ├── DataLoader.java       # Seed development users and sample recipes
        │       │   └── SecurityConfig.java   # Password encoder and security filters
        │       │
        │       ├── controller/
        │       │   ├── AuthController.java   # Authentication endpoints
        │       │   ├── RecipeController.java # Recipe catalog and CRUD endpoints
        │       │   ├── AdminController.java  # Admin approval & rejection endpoints
        │       │   ├── ReviewController.java # Ratings and reviews endpoints
        │       │   └── SavedRecipeController.java # Bookmark management endpoints
        │       │
        │       ├── service/
        │       │   ├── UserService.java      # User registration and verification logic
        │       │   ├── RecipeService.java    # Recipe CRUD and status logic
        │       │   ├── ReviewService.java    # Rating calculation and review logic
        │       │   └── SavedRecipeService.java # Bookmark business logic
        │       │
        │       ├── repository/
        │       │   ├── UserRepository.java   # User data access
        │       │   ├── RecipeRepository.java # Recipe data access
        │       │   ├── ReviewRepository.java # Review data access
        │       │   └── SavedRecipeRepository.java # Bookmark data access
        │       │
        │       └── model/
        │           ├── User.java             # Entity for 'users' table
        │           ├── Recipe.java           # Entity for 'recipes' table
        │           ├── Review.java           # Entity for 'reviews' table
        │           └── SavedRecipe.java      # Entity for 'saved_recipes' table
        │
        └── resources/
            ├── application.properties        # Application configurations and JDBC URL
            └── static/                       # Frontend web files
                ├── index.html                # Home page with featured recipes
                ├── recipes.html              # Searchable recipe catalog
                ├── recipe-details.html       # Single recipe view, reviews & bookmarks
                ├── login.html                # User login page
                ├── register.html             # User registration page
                ├── add-recipe.html           # Recipe submission & editing form
                ├── my-recipes.html           # Contributor dashboard
                ├── saved-recipes.html        # Explorer bookmark dashboard
                ├── admin.html                # Admin recipe moderation queue
                ├── css/
                │   └── style.css             # Main styling stylesheet
                └── js/
                    ├── common.js             # Shared navigation and session helpers
                    ├── index.js              # Home page controller
                    ├── recipes.js            # Catalog search and filter controller
                    ├── recipe-details.js     # Detail view, reviews, and bookmarking
                    ├── login.js              # Login handler
                    ├── register.js           # Registration handler
                    ├── add-recipe.js         # Submission and edit handler
                    ├── my-recipes.js         # Contributor recipe management
                    ├── saved-recipes.js      # Saved recipes controller
                    └── admin.js              # Admin moderation queue handler
```

---

## Database

RecipeHub connects to MySQL on database:

`recipehub`

The schema is automatically managed by Hibernate (`spring.jpa.hibernate.ddl-auto=update`) and comprises four primary tables:

1. **`users`**: Stores user ID, name, email, role (`ADMIN`, `CONTRIBUTOR`, `EXPLORER`), and BCrypt password hash.
2. **`recipes`**: Stores recipe title, description, ingredients, instructions, category, image URL, status (`PENDING`, `APPROVED`, `REJECTED`), author info, and computed average rating.
3. **`reviews`**: Stores individual ratings (1–5 stars), optional feedback comments, user ID, author name, and associated recipe ID.
4. **`saved_recipes`**: Stores bookmark relationships linking user IDs to recipe IDs.

---

## Setup Instructions

### Prerequisites
1. **Java Development Kit (JDK 17 or higher)**
   Verify installation:
   ```powershell
   java -version
   ```
2. **Apache Maven (3.8 or higher)**
   Verify installation:
   ```powershell
   mvn -version
   ```
3. **MySQL Server 8.0**
   Ensure MySQL service is running on port 3306.

---

### Step 1: Clone the Repository
```powershell
git clone https://github.com/Parth-verse/RecipeHub.git
cd RecipeHub
```

### Step 2: Configure Environment Variables
Set your MySQL root password in your environment (do not commit your password to version control):

**PowerShell (Windows):**
```powershell
$env:MYSQL_PASSWORD="YOUR_MYSQL_PASSWORD"
```

**Command Prompt (cmd):**
```cmd
set MYSQL_PASSWORD=YOUR_MYSQL_PASSWORD
```

**Bash / macOS / Linux:**
```bash
export MYSQL_PASSWORD="YOUR_MYSQL_PASSWORD"
```

> **Note**: The JDBC URL in `application.properties` uses `createDatabaseIfNotExist=true`. If MySQL is running and credentials are valid, the `recipehub` database will be created automatically upon initial launch.

### Step 3: Build the Project
```powershell
mvn clean compile
```

### Step 4: Run the Application
```powershell
mvn spring-boot:run
```

### Step 5: Access the Web Application
Open your browser and navigate to:
```text
http://localhost:8085
```

---

## Development / Test Accounts

For demonstration and local development, the application seeds the following test accounts on first launch:

| Role | Email Address | Password | Intended Actions |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin@recipehub.com` | `admin123` | Moderate pending submissions on `/admin.html` |
| **Contributor** | `chef@recipehub.com` | `chef123` | Submit recipes on `/add-recipe.html`, manage on `/my-recipes.html` |
| **Explorer** | `alice@recipehub.com` | `alice123` | Browse catalog, rate recipes, bookmark favorites |

*(Quick-fill buttons are available directly on the login page for convenience).*

---

## API Endpoints

### Authentication (`/api/auth`)
* `POST /api/auth/register` — Register a new account (`name`, `email`, `password`, `role`)
* `POST /api/auth/login` — Authenticate and establish session
* `POST /api/auth/logout` — Terminate current session
* `GET  /api/auth/current` — Get profile of currently logged-in user

### Recipes (`/api/recipes`)
* `GET    /api/recipes` — List approved recipes (supports optional `?search=` and `?category=`)
* `GET    /api/recipes/{id}` — Get single recipe details
* `GET    /api/recipes/my` — Get all recipes created by current contributor
* `POST   /api/recipes` — Submit new recipe (initial status: `PENDING`)
* `PUT    /api/recipes/{id}` — Update existing recipe (owner or Admin)
* `DELETE /api/recipes/{id}` — Delete existing recipe (owner or Admin)

### Admin Moderation (`/api/admin`)
* `GET /api/admin/recipes/pending` — List all recipes awaiting review
* `PUT /api/admin/recipes/{id}/approve` — Approve recipe (status set to `APPROVED`)
* `PUT /api/admin/recipes/{id}/reject` — Reject recipe (status set to `REJECTED`)

### Reviews & Ratings (`/api`)
* `GET    /api/recipes/{id}/reviews` — Get all reviews for a recipe
* `POST   /api/recipes/{id}/reviews` — Submit a rating (1–5) and comment
* `DELETE /api/reviews/{id}` — Delete a review and recalculate recipe rating

### Saved Recipes / Bookmarks (`/api`)
* `GET    /api/saved-recipes` — Get all bookmarked recipes for current user
* `POST   /api/recipes/{id}/save` — Bookmark a recipe
* `DELETE /api/recipes/{id}/save` — Remove recipe from bookmarks
* `GET    /api/recipes/{id}/is-saved` — Check bookmark status for recipe

---

## Future Improvements

* **Cloud Image Storage**: Direct photo uploads via AWS S3 or Cloudinary instead of external image URLs.
* **Pagination & Infinite Scroll**: Page-based queries for recipe catalogs with large datasets.
* **Email Verification**: Account activation and password reset via SMTP.
* **Nutritional Information**: Breakdown of calories, macronutrients, and allergen warnings.
* **Smart Recommendations**: Content-based recipe recommendations based on user bookmark history.
* **Containerized Deployment**: Docker containerization and Kubernetes orchestration.

---

## Author

**Parth Sharma**  
B.Tech CSE (AI & ML)  
Galgotias University
