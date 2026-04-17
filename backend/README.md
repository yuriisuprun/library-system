# Library Backend

A Spring Boot application for managing a library book collection with modern database migration and data loading capabilities.

## Features

- **RESTful API** for book management (CRUD operations)
- **Database Migrations** using Flyway
- **Automatic Data Loading** with initial sample books
- **Multiple Environment Support** (dev, prod)
- **Comprehensive Logging** and monitoring
- **H2 Database** with file persistence
- **Audit Fields** for tracking creation and updates

## Quick Start

### Development Mode

```powershell
# From project root
.\scripts\start-backend-dev.ps1
```

Or manually:

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Production Mode

```powershell
# From project root
.\scripts\start-backend-prod.ps1 -Build
```

## Database Setup

### Flyway Migrations

The application uses Flyway for database schema management:

- **V1__Create_books_table.sql**: Creates the main books table with indexes
- **V2__Insert_initial_books.sql**: Loads initial sample books

Migrations are automatically applied on application startup.

### Initial Data

The application comes with two sample books:

1. **"Lo cercava Elia"** by Ugo Grottoli (2023)
2. **"Bla! bla!"** by Scrittore Moderno (2024)

These books are loaded via:
1. Flyway migration (V2) - Primary method
2. DataLoaderService - Backup/development method

## Configuration

### Environment Profiles

- **Default**: Basic configuration with in-memory H2
- **dev**: Development with file-based H2, enhanced logging, H2 console enabled
- **prod**: Production-ready with security hardening, minimal logging

### Database Access

#### Development Mode
- **H2 Console**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:file:./data/librarydb`
- **Username**: `sa`
- **Password**: (empty)

#### API Endpoints
- **Base URL**: http://localhost:8080/api
- **Books**: `/api/books`
- **Health Check**: http://localhost:8080/actuator/health

## API Documentation

### Book Management

#### Get All Books
```http
GET /api/books
```

#### Get Book by ID
```http
GET /api/books/{id}
```

#### Create Book
```http
POST /api/books
Content-Type: application/json

{
  "title": "Book Title",
  "author": "Author Name",
  "isbn": "978-1234567890",
  "publishedYear": 2024,
  "publisher": "Publisher Name",
  "genre": "Fiction",
  "targetAudience": "ADULT",
  "country": "US",
  "language": "en",
  "pageCount": 300,
  "description": "Book description"
}
```

#### Update Book
```http
PUT /api/books/{id}
Content-Type: application/json

{
  "title": "Updated Title",
  "author": "Updated Author"
}
```

#### Delete Book
```http
DELETE /api/books/{id}
```

#### Search Books
```http
GET /api/books/search?query=keyword
GET /api/books/search/title?title=book-title
GET /api/books/search/author?author=author-name
GET /api/books/search/isbn?isbn=978-1234567890
GET /api/books/search/year?year=2024
```

## Database Schema

### Books Table

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| title | VARCHAR(255) | NOT NULL | Book title |
| author | VARCHAR(255) | NOT NULL | Author name |
| isbn | VARCHAR(20) | | ISBN number |
| published_year | INTEGER | >= 0 | Publication year |
| publisher | VARCHAR(255) | | Publisher name |
| genre | VARCHAR(100) | | Book genre |
| target_audience | VARCHAR(30) | ENUM | Target audience |
| country | VARCHAR(100) | | Country of publication |
| language | VARCHAR(50) | | Language code |
| page_count | INTEGER | >= 1 | Number of pages |
| description | VARCHAR(2000) | | Book description |
| cover_image_url | VARCHAR(500) | | Cover image URL |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Creation timestamp |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP ON UPDATE | Last update timestamp |

### Indexes

- `idx_books_title` on `title`
- `idx_books_author` on `author`
- `idx_books_isbn` on `isbn`
- `idx_books_published_year` on `published_year`
- `idx_books_genre` on `genre`

## Development

### Prerequisites

- Java 21+
- Maven 3.6+
- Git

### Building

```bash
# Clean build
mvn clean compile

# Run tests
mvn test

# Package
mvn package

# Skip tests
mvn package -DskipTests
```

### Database Management

#### View Migration Status
```bash
mvn flyway:info
```

#### Validate Migrations
```bash
mvn flyway:validate
```

#### Clean Database (Development Only)
```bash
mvn flyway:clean
```

## Monitoring

### Health Checks

- **Application Health**: `/actuator/health`
- **Database Status**: `/actuator/flyway`
- **Application Info**: `/actuator/info`

### Logging

Logs are written to:
- **Development**: `./logs/library-dev.log`
- **Production**: `/opt/library/logs/library.log`

## Best Practices Implemented

1. **Database Migrations**: Versioned, repeatable schema changes
2. **Environment Separation**: Different configs for dev/prod
3. **Audit Fields**: Track creation and modification times
4. **Idempotent Operations**: Safe to run multiple times
5. **Comprehensive Logging**: Detailed application monitoring
6. **Error Handling**: Graceful failure management
7. **Security**: Production hardening and access control
8. **Performance**: Database indexing for common queries

## Troubleshooting

### Common Issues

1. **Port Already in Use**: Change server.port in application.yml
2. **Database Lock**: Stop all H2 connections and restart
3. **Migration Failures**: Check Flyway logs and validate SQL syntax
4. **Permission Issues**: Ensure write access to data/logs directories

### Debug Mode

Enable debug logging:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev -Dlogging.level.com.example.library=DEBUG
```