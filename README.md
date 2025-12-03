# Certificate Management System

A comprehensive certificate management solution built with Angular frontend and Spring Boot backend, designed for organizations to securely generate and manage PDF certificates.

## 🏗️ Architecture

### System Overview
- **Frontend**: Angular 19 with TypeScript
- **Backend**: Spring Boot 3.4 with Java 17
- **Database**: PostgreSQL
- **PDF Generation**: OpenPDF library
- **Authentication**: API Key-based authentication
- **Testing**: JUnit 5 with JaCoCo coverage

### Key Components
- **Customer Management**: Multi-tenant architecture with API key isolation
- **Template System**: HTML-based certificate templates with placeholder replacement
- **Certificate Generation**: Async PDF generation with storage
- **Verification System**: Hash-based certificate authenticity verification
- **Security**: API key authentication with CORS support

## 🎯 Features

### Core Use Cases
1. **Customer Onboarding**: Register new customers with unique API keys
2. **Template Management**: Create and manage certificate templates
3. **Certificate Simulation**: Preview certificates before generation
4. **Certificate Generation**: Generate PDF certificates via API
5. **Certificate Download**: Secure PDF certificate retrieval
6. **Verification**: Public verification of certificate authenticity

### Security & Scalability
- **Multi-tenancy**: Complete customer data isolation
- **API Security**: API key authentication for all operations
- **Fraud Prevention**: Cryptographic hash verification system
- **Performance**: Async processing for high-volume generation (>1000 certs/min)
- **CORS Support**: Configured for Angular frontend integration

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Node.js 18+
- PostgreSQL 12+
- Maven 3.6+

### Backend Setup

1. **Clone and navigate to backend**:
   ```bash
   cd backend/sec-certificate-api
   ```

2. **Configure Database**:
   Update `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/certificates
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Run tests**:
   ```bash
   ./mvnw test
   ```

### Frontend Setup

1. **Navigate to frontend**:
   ```bash
   cd frontend/sec-certificate-ui
   ```

2. **Install dependencies**:
   ```bash
   npm install
   ```

3. **Start development server**:
   ```bash
   npm start
   ```

4. **Build for production**:
   ```bash
   npm run build
   ```

## 📋 API Documentation

### Authentication
All API requests require an `x-api-key` header with the customer's API key.

### Endpoints

#### Customer Management
- `POST /api/customers` - Onboard new customer
- `GET /api/customers` - List all customers (admin)

#### Template Management
- `POST /api/templates` - Create certificate template
- `GET /api/templates` - List customer's templates

#### Certificate Operations
- `POST /api/certificates/simulate` - Simulate certificate generation
- `POST /api/certificates/generate` - Generate PDF certificate
- `GET /api/certificates/{id}/download` - Download PDF certificate

#### Verification
- `GET /api/verify/{hash}` - Verify certificate authenticity (public)

## 🧪 Testing

### Backend Testing
```bash
cd backend/sec-certificate-api
./mvnw test
```

**Test Coverage**: JaCoCo generates coverage reports in `target/site/jacoco/`

### Frontend Testing
```bash
cd frontend/sec-certificate-ui
npm test
```

## 🔒 Security Features

### Multi-Tenant Isolation
- Each customer has a unique API key
- Database relationships ensure data isolation
- Authentication middleware validates API keys

### Certificate Security
- Generated PDFs stored securely on filesystem
- Verification hashes prevent tampering
- Public verification endpoint for authenticity checks

### API Security
- API key authentication required for all operations
- CORS configured for frontend integration
- Input validation and error handling

## 📊 Performance & Scalability

### High-Volume Generation
- `@EnableAsync` for concurrent processing
- Database optimization with proper indexing
- File storage for PDF artifacts

### Database Schema
- PostgreSQL with foreign key constraints
- Unique constraints on API keys and verification hashes
- Timestamp tracking for audit trails

## 🛠️ Development

### Project Structure
```
certificate-management/
├── backend/sec-certificate-api/     # Spring Boot application
│   ├── src/main/java/com/seccertificate/api/
│   │   ├── controller/              # REST controllers
│   │   ├── service/                 # Business logic
│   │   ├── repository/              # Data access
│   │   ├── domain/                  # JPA entities
│   │   ├── dto/                     # Data transfer objects
│   │   ├── security/                # Authentication
│   │   └── config/                  # Configuration
│   └── src/test/                    # Test suites
└── frontend/sec-certificate-ui/     # Angular application
    ├── src/app/
    │   ├── core/                    # Core services
    │   ├── features/                # Feature modules
    │   └── layout/                  # Layout components
    └── public/                      # Static assets
```

### Key Technologies
- **Backend**: Spring Boot, Spring Security, Spring Data JPA, OpenPDF
- **Frontend**: Angular, TypeScript, RxJS
- **Database**: PostgreSQL with H2 for testing
- **Build Tools**: Maven, npm
- **Testing**: JUnit 5, JaCoCo, Karma, Jasmine

## 📈 Monitoring & Quality

### Code Quality
- JaCoCo code coverage reporting
- Comprehensive unit and integration tests
- Static analysis with Maven plugins

### Logging
- Structured logging with Spring Boot
- Request/response logging for debugging
- Error tracking and monitoring

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make changes with tests
4. Ensure all tests pass
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For questions or issues, please create an issue in the repository or contact the development team.
