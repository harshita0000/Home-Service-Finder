# Urban Services - On-Demand Service Provider Marketplace

A comprehensive Spring Boot application for connecting customers with service providers for various on-demand services like plumbing, electrical work, cleaning, and more.

## 🚀 Features

### Core Functionality
- **User Management**: Registration, authentication, and profile management
- **Service Categories**: Browse different service categories (Plumbing, Electrical, Cleaning, etc.)
- **Service Provider Listings**: View and filter available service providers
- **Booking System**: Schedule appointments with service providers
- **Role-based Access**: Different dashboards for Customers, Service Providers, and Admins
- **Real-time Chat**: Communication between customers and providers
- **Rating & Reviews**: Rate and review service providers

### Technical Features
- **Spring Boot 3.1.0**: Modern Java web framework
- **Spring Security**: Authentication and authorization
- **JWT Authentication**: Token-based security
- **Thymeleaf Templates**: Server-side rendering
- **H2 Database**: In-memory database for development
- **JPA/Hibernate**: Object-relational mapping
- **Bootstrap 5**: Responsive UI framework
- **OAuth2 Integration**: Social login (Google, Facebook)

## 🛠️ Technology Stack

- **Backend**: Java 17, Spring Boot 3.1.0
- **Frontend**: Thymeleaf, HTML5, CSS3, JavaScript
- **Security**: Spring Security, JWT
- **Database**: H2 (development), PostgreSQL/MySQL (production ready)
- **Build Tool**: Maven
- **UI Framework**: Bootstrap 5, Font Awesome

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Git

## 🏃‍♂️ Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/urban-services.git
cd urban-services
```

### 2. Run the Application
```bash
./mvnw spring-boot:run
```

### 3. Access the Application
- **Main Application**: http://localhost:8080
- **H2 Database Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: *(leave empty)*

## 🔧 Configuration

### Database Configuration
The application uses H2 in-memory database by default. To configure other databases:

```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/urbanservices
spring.datasource.username=your_username
spring.datasource.password=your_password

# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/urbanservices
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### JWT Configuration
```properties
jwt.secret=your-secret-key
jwt.expiration=86400000
```

## 👥 User Roles

### Customer
- Browse service categories
- View service provider profiles
- Book appointments
- Chat with providers
- Rate and review services

### Service Provider
- Manage availability
- View booking requests
- Communicate with customers
- Update profile and services

### Admin
- User management
- System analytics
- Category management
- Platform oversight

## 🗂️ Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── org/example/apcproject3/
│   │       ├── config/          # Configuration classes
│   │       ├── controller/      # Web and REST controllers
│   │       ├── entity/          # JPA entities
│   │       ├── repository/      # Data repositories
│   │       ├── service/         # Business logic
│   │       ├── security/        # Security configuration
│   │       └── dto/            # Data transfer objects
│   └── resources/
│       ├── templates/          # Thymeleaf templates
│       ├── static/            # Static assets (CSS, JS, images)
│       └── application.properties
```

## 🔐 Security Features

- Password encryption using BCrypt
- JWT token-based authentication
- CSRF protection
- XSS prevention
- Role-based access control
- OAuth2 social login integration

## 🚀 Deployment

### Development
```bash
./mvnw spring-boot:run
```

### Production
```bash
./mvnw clean package
java -jar target/apcproject3-0.0.1-SNAPSHOT.jar
```

## 🧪 Testing

### Run Tests
```bash
./mvnw test
```

### Run Specific Test Classes
```bash
./mvnw test -Dtest=UserServiceTest
```

## 📊 Database Schema

Key entities:
- **Users**: Customer and provider information
- **ServiceCategory**: Available service types
- **ServiceProvider**: Provider details and availability
- **Booking**: Appointment and service requests
- **Review**: Customer feedback and ratings

## 🔄 API Endpoints

### Authentication
- `POST /api/auth/signup` - User registration
- `POST /api/auth/signin` - User login
- `GET /api/auth/me` - Get current user

### Services
- `GET /services` - Browse service categories
- `GET /services/category/{id}` - View providers by category
- `GET /provider/{id}` - View provider details

### Bookings
- `POST /api/bookings` - Create booking
- `GET /my-bookings` - View user bookings

## 🛡️ Known Issues & Fixes Applied

1. **Thymeleaf Security Error**: Fixed by replacing unsafe `th:onclick` with `data-*` attributes
2. **Password Encoding**: Resolved double encoding issues in registration
3. **Database Dependencies**: Temporarily disabled MongoDB for simplified setup

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For support and questions:
- Create an issue in this repository
- Contact the development team

## 🔮 Future Enhancements

- [ ] Real-time notifications
- [ ] Payment integration
- [ ] Mobile app development
- [ ] Advanced analytics dashboard
- [ ] Multi-language support
- [ ] Geolocation-based provider search

---

**Made with ❤️ using Spring Boot and modern web technologies**
