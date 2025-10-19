package org.example.apcproject3.config;

import org.example.apcproject3.entity.*;
import org.example.apcproject3.service.ServiceCategoryService;
import org.example.apcproject3.service.ServiceProviderService;
import org.example.apcproject3.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ServiceCategoryService serviceCategoryService;

    @Autowired
    private ServiceProviderService serviceProviderService;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Initializing sample data...");

        try {
            // Create sample users if they don't exist
            createSampleUsers();

            // Create sample service categories
            createSampleServiceCategories();

            // Create sample service providers
            createSampleServiceProviders();

            logger.info("Sample data initialization completed successfully!");

        } catch (Exception e) {
            logger.warn("Some sample data may already exist or there was an error: {}", e.getMessage());
        }
    }

    private void createSampleUsers() {
        // Create admin user
        if (!userService.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@urbanservices.com");
            admin.setPassword("admin123"); // Will be encoded by UserService
            admin.setFirstName("System");
            admin.setLastName("Administrator");
            admin.setRole(UserRole.ADMIN);
            admin.setPhoneNumber("+1-555-0001");
            admin.setAddress("123 Admin Street, City, State 12345");

            userService.createUser(admin);
            logger.info("Created admin user: admin / admin123");
        }

        // Create test customer
        if (!userService.existsByUsername("customer")) {
            User customer = new User();
            customer.setUsername("customer");
            customer.setEmail("customer@example.com");
            customer.setPassword("customer123");
            customer.setFirstName("John");
            customer.setLastName("Doe");
            customer.setRole(UserRole.CUSTOMER);
            customer.setPhoneNumber("+1-555-0002");
            customer.setAddress("456 Customer Ave, City, State 12345");

            userService.createUser(customer);
            logger.info("Created customer user: customer / customer123");
        }

        // Create test service provider
        if (!userService.existsByUsername("provider")) {
            User providerUser = new User();
            providerUser.setUsername("provider");
            providerUser.setEmail("provider@example.com");
            providerUser.setPassword("provider123");
            providerUser.setFirstName("Mike");
            providerUser.setLastName("Smith");
            providerUser.setRole(UserRole.SERVICE_PROVIDER);
            providerUser.setPhoneNumber("+1-555-0003");
            providerUser.setAddress("789 Provider Rd, City, State 12345");

            userService.createUser(providerUser);
            logger.info("Created service provider user: provider / provider123");
        }

        // Create additional test users
        if (!userService.existsByEmail("jane@example.com")) {
            User jane = new User();
            jane.setUsername("jane.doe");
            jane.setEmail("jane@example.com");
            jane.setPassword("password123");
            jane.setFirstName("Jane");
            jane.setLastName("Doe");
            jane.setRole(UserRole.CUSTOMER);
            jane.setPhoneNumber("+1-555-0004");

            userService.createUser(jane);
            logger.info("Created customer user: jane.doe / password123");
        }
    }

    private void createSampleServiceCategories() {
        createCategoryIfNotExists("Home Cleaning", "Professional home cleaning services", "fas fa-broom");
        createCategoryIfNotExists("Plumbing", "Expert plumbing repair and installation services", "fas fa-wrench");
        createCategoryIfNotExists("Electrical", "Licensed electrical services for your home", "fas fa-bolt");
        createCategoryIfNotExists("Gardening", "Landscaping and garden maintenance services", "fas fa-seedling");
        createCategoryIfNotExists("Carpentry", "Custom woodwork and furniture repair", "fas fa-hammer");
        createCategoryIfNotExists("Painting", "Interior and exterior painting services", "fas fa-paint-roller");
        createCategoryIfNotExists("HVAC", "Heating, ventilation, and air conditioning services", "fas fa-fan");
        createCategoryIfNotExists("Auto Repair", "Professional automotive repair services", "fas fa-car");
    }

    private void createCategoryIfNotExists(String name, String description, String icon) {
        if (!serviceCategoryService.existsByName(name)) {
            ServiceCategory category = new ServiceCategory();
            category.setName(name);
            category.setDescription(description);
            category.setIcon(icon);
            category.setActive(true);

            serviceCategoryService.createCategory(category);
            logger.info("Created service category: {}", name);
        }
    }

    private void createSampleServiceProviders() {
        try {
            // Create providers for each category
            createProvidersForAllCategories();

            // Find the provider user and create a service provider profile
            userService.findByUsername("provider").ifPresent(providerUser -> {
                try {
                    if (!serviceProviderService.findByUser(providerUser).isPresent()) {
                        // Find a category for the provider
                        serviceCategoryService.findByName("Home Cleaning").ifPresent(category -> {
                            try {
                                ServiceProvider provider = new ServiceProvider();
                                provider.setUser(providerUser);
                                provider.setCategory(category);
                                provider.setBio("Experienced home cleaning professional with 5+ years of experience. " +
                                              "Reliable, thorough, and eco-friendly cleaning solutions.");
                                provider.setExperienceYears(new BigDecimal("5.5"));
                                provider.setHourlyRate(new BigDecimal("25.00"));
                                provider.setRating(new BigDecimal("4.8"));
                                provider.setTotalReviews(24);
                                provider.setAvailable(true);
                                provider.setVerified(true);

                                serviceProviderService.createProvider(provider);
                                logger.info("Created service provider profile for: {}", providerUser.getUsername());

                            } catch (Exception e) {
                                logger.warn("Could not create service provider: {}", e.getMessage());
                            }
                        });
                    }
                } catch (Exception e) {
                    logger.warn("Error creating service provider profile: {}", e.getMessage());
                }
            });
        } catch (Exception e) {
            logger.warn("Error in createSampleServiceProviders: {}", e.getMessage());
        }
    }

    private void createProvidersForAllCategories() {
        // Home Cleaning Providers
        createProviderForCategory("Home Cleaning", "Maria", "Garcia", "maria.garcia", "maria.garcia@cleaning.com",
            "Professional house cleaner with 8 years experience in eco-friendly cleaning", 28.0, 8, 4.9f);
        createProviderForCategory("Home Cleaning", "Carlos", "Rodriguez", "carlos.rodriguez", "carlos.rodriguez@cleaning.com",
            "Deep cleaning specialist for residential and commercial spaces", 30.0, 6, 4.7f);
        createProviderForCategory("Home Cleaning", "Amanda", "Wilson", "amanda.wilson", "amanda.wilson@cleaning.com",
            "Move-in/move-out cleaning expert with attention to detail", 26.0, 5, 4.8f);
        createProviderForCategory("Home Cleaning", "Roberto", "Martinez", "roberto.martinez", "roberto.martinez@cleaning.com",
            "Window and carpet cleaning specialist", 24.0, 4, 4.6f);
        createProviderForCategory("Home Cleaning", "Lisa", "Thompson", "lisa.thompson", "lisa.thompson@cleaning.com",
            "Office cleaning and sanitization expert", 32.0, 10, 4.8f);
        createProviderForCategory("Home Cleaning", "David", "Lee", "david.lee", "david.lee@cleaning.com",
            "Post-construction cleanup and restoration specialist", 35.0, 7, 4.5f);

        // Plumbing Providers
        createProviderForCategory("Plumbing", "John", "Smith", "john.smith.plumber", "john.smith@plumbing.com",
            "Master plumber with 15+ years experience in residential and commercial plumbing", 55.0, 15, 4.9f);
        createProviderForCategory("Plumbing", "Sarah", "Johnson", "sarah.johnson", "sarah.johnson@plumbing.com",
            "Emergency plumber available 24/7 for urgent repairs", 50.0, 12, 4.8f);
        createProviderForCategory("Plumbing", "Michael", "Brown", "michael.brown", "michael.brown@plumbing.com",
            "Bathroom and kitchen remodeling plumbing specialist", 48.0, 10, 4.7f);
        createProviderForCategory("Plumbing", "Jennifer", "Davis", "jennifer.davis", "jennifer.davis@plumbing.com",
            "Water heater installation and repair expert", 45.0, 8, 4.6f);
        createProviderForCategory("Plumbing", "Robert", "Wilson", "robert.wilson", "robert.wilson@plumbing.com",
            "Sewer line and drain cleaning professional", 52.0, 18, 4.8f);
        createProviderForCategory("Plumbing", "Emily", "Moore", "emily.moore", "emily.moore@plumbing.com",
            "Eco-friendly plumbing solutions and water conservation", 46.0, 9, 4.5f);

        // Electrical Providers
        createProviderForCategory("Electrical", "Alex", "Taylor", "alex.taylor", "alex.taylor@electrical.com",
            "Licensed electrician specializing in home automation and smart systems", 65.0, 12, 4.9f);
        createProviderForCategory("Electrical", "Jessica", "Anderson", "jessica.anderson", "jessica.anderson@electrical.com",
            "Electrical panel upgrades and code compliance expert", 58.0, 14, 4.8f);
        createProviderForCategory("Electrical", "Daniel", "Thomas", "daniel.thomas", "daniel.thomas@electrical.com",
            "Solar panel installation and renewable energy specialist", 62.0, 10, 4.7f);
        createProviderForCategory("Electrical", "Michelle", "Jackson", "michelle.jackson", "michelle.jackson@electrical.com",
            "Emergency electrical services and troubleshooting", 60.0, 16, 4.8f);
        createProviderForCategory("Electrical", "Christopher", "White", "christopher.white", "christopher.white@electrical.com",
            "Commercial electrical contractor for large projects", 68.0, 20, 4.9f);
        createProviderForCategory("Electrical", "Ashley", "Harris", "ashley.harris", "ashley.harris@electrical.com",
            "LED lighting and energy-efficient electrical solutions", 55.0, 7, 4.6f);

        // Gardening Providers
        createProviderForCategory("Gardening", "Grace", "Martin", "grace.martin", "grace.martin@gardening.com",
            "Landscape designer with expertise in sustainable and native plant gardens", 38.0, 12, 4.8f);
        createProviderForCategory("Gardening", "Peter", "Garcia", "peter.garcia", "peter.garcia@gardening.com",
            "Lawn care and maintenance specialist with organic methods", 32.0, 15, 4.7f);
        createProviderForCategory("Gardening", "Amanda", "Rodriguez", "amanda.rodriguez", "amanda.rodriguez@gardening.com",
            "Tree service and pruning expert", 40.0, 18, 4.9f);
        createProviderForCategory("Gardening", "Kevin", "Lewis", "kevin.lewis", "kevin.lewis@gardening.com",
            "Irrigation system installation and water management", 42.0, 10, 4.6f);
        createProviderForCategory("Gardening", "Sophia", "Walker", "sophia.walker", "sophia.walker@gardening.com",
            "Vegetable garden and greenhouse specialist", 35.0, 8, 4.8f);
        createProviderForCategory("Gardening", "Tyler", "Hall", "tyler.hall", "tyler.hall@gardening.com",
            "Hardscaping and outdoor living space design", 45.0, 14, 4.5f);

        // Carpentry Providers
        createProviderForCategory("Carpentry", "Matthew", "Allen", "matthew.allen", "matthew.allen@carpentry.com",
            "Custom cabinet maker and kitchen renovation specialist", 52.0, 16, 4.9f);
        createProviderForCategory("Carpentry", "Rachel", "Young", "rachel.young", "rachel.young@carpentry.com",
            "Fine furniture restoration and antique repair expert", 58.0, 20, 4.8f);
        createProviderForCategory("Carpentry", "Joshua", "King", "joshua.king", "joshua.king@carpentry.com",
            "Deck building and outdoor structure specialist", 48.0, 12, 4.7f);
        createProviderForCategory("Carpentry", "Nicole", "Wright", "nicole.wright", "nicole.wright@carpentry.com",
            "Built-in shelving and custom storage solutions", 45.0, 9, 4.6f);
        createProviderForCategory("Carpentry", "Brandon", "Lopez", "brandon.lopez", "brandon.lopez@carpentry.com",
            "Trim work and crown molding installation expert", 42.0, 11, 4.8f);
        createProviderForCategory("Carpentry", "Stephanie", "Hill", "stephanie.hill", "stephanie.hill@carpentry.com",
            "Custom closet and wardrobe design specialist", 50.0, 8, 4.5f);

        // Painting Providers
        createProviderForCategory("Painting", "Andrew", "Scott", "andrew.scott", "andrew.scott@painting.com",
            "Interior and exterior painting contractor with color consultation", 42.0, 12, 4.8f);
        createProviderForCategory("Painting", "Laura", "Green", "laura.green", "laura.green@painting.com",
            "Decorative painting and mural artist", 48.0, 8, 4.9f);
        createProviderForCategory("Painting", "James", "Adams", "james.adams", "james.adams@painting.com",
            "Commercial painting and large-scale projects", 40.0, 15, 4.7f);
        createProviderForCategory("Painting", "Megan", "Baker", "megan.baker", "megan.baker@painting.com",
            "Eco-friendly paint specialist and health-conscious solutions", 45.0, 6, 4.6f);
        createProviderForCategory("Painting", "Ryan", "Gonzalez", "ryan.gonzalez", "ryan.gonzalez@painting.com",
            "Cabinet refinishing and furniture painting expert", 38.0, 10, 4.8f);
        createProviderForCategory("Painting", "Victoria", "Nelson", "victoria.nelson", "victoria.nelson@painting.com",
            "Pressure washing and surface preparation specialist", 35.0, 7, 4.5f);

        // HVAC Providers
        createProviderForCategory("HVAC", "Thomas", "Carter", "thomas.carter", "thomas.carter@hvac.com",
            "HVAC installation and repair specialist with energy efficiency focus", 62.0, 15, 4.8f);
        createProviderForCategory("HVAC", "Patricia", "Mitchell", "patricia.mitchell", "patricia.mitchell@hvac.com",
            "Commercial HVAC systems and industrial cooling expert", 68.0, 18, 4.9f);
        createProviderForCategory("HVAC", "Richard", "Perez", "richard.perez", "richard.perez@hvac.com",
            "Emergency HVAC repairs and 24/7 service availability", 58.0, 12, 4.7f);
        createProviderForCategory("HVAC", "Jennifer", "Roberts", "jennifer.roberts", "jennifer.roberts@hvac.com",
            "Heat pump and geothermal system installation specialist", 65.0, 14, 4.8f);
        createProviderForCategory("HVAC", "Charles", "Turner", "charles.turner", "charles.turner@hvac.com",
            "Ductwork installation and air quality improvement", 55.0, 10, 4.6f);
        createProviderForCategory("HVAC", "Linda", "Phillips", "linda.phillips", "linda.phillips@hvac.com",
            "Smart thermostat and home automation HVAC integration", 60.0, 8, 4.7f);

        // Auto Repair Providers
        createProviderForCategory("Auto Repair", "Mark", "Campbell", "mark.campbell", "mark.campbell@auto.com",
            "ASE certified mechanic with expertise in engine diagnostics", 75.0, 20, 4.9f);
        createProviderForCategory("Auto Repair", "Susan", "Parker", "susan.parker", "susan.parker@auto.com",
            "Transmission and brake system specialist", 70.0, 15, 4.8f);
        createProviderForCategory("Auto Repair", "Jason", "Evans", "jason.evans", "jason.evans@auto.com",
            "Mobile mechanic providing on-site automotive services", 65.0, 12, 4.7f);
        createProviderForCategory("Auto Repair", "Michelle", "Edwards", "michelle.edwards", "michelle.edwards@auto.com",
            "Hybrid and electric vehicle specialist", 80.0, 8, 4.8f);
        createProviderForCategory("Auto Repair", "Brian", "Collins", "brian.collins", "brian.collins@auto.com",
            "Classic car restoration and vintage automobile expert", 85.0, 25, 4.9f);
        createProviderForCategory("Auto Repair", "Kimberly", "Stewart", "kimberly.stewart", "kimberly.stewart@auto.com",
            "Auto body repair and collision damage specialist", 72.0, 14, 4.6f);
    }

    private void createProviderForCategory(String categoryName, String firstName, String lastName,
                                         String username, String email, String bio,
                                         double hourlyRate, int experience, float rating) {
        try {
            // Check if user already exists
            if (userService.existsByUsername(username) || userService.existsByEmail(email)) {
                return; // Skip if already exists
            }

            // Find the category
            serviceCategoryService.findByName(categoryName).ifPresent(category -> {
                try {
                    // Create user
                    User user = new User();
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setUsername(username);
                    user.setEmail(email);
                    user.setPassword("password123"); // Will be encoded by UserService
                    user.setRole(UserRole.SERVICE_PROVIDER);
                    user.setPhoneNumber(generatePhoneNumber());
                    user.setAddress(generateAddress());

                    User savedUser = userService.createUser(user);

                    // Create service provider
                    ServiceProvider provider = new ServiceProvider();
                    provider.setUser(savedUser);
                    provider.setCategory(category);
                    provider.setBio(bio);
                    provider.setExperienceYears(new BigDecimal(experience));
                    provider.setHourlyRate(new BigDecimal(hourlyRate));
                    provider.setRating(new BigDecimal(rating));
                    provider.setTotalReviews((int)(Math.random() * 50) + 10);
                    provider.setAvailable(Math.random() > 0.15); // 85% available
                    provider.setVerified(Math.random() > 0.2); // 80% verified

                    serviceProviderService.createProvider(provider);
                    logger.info("✅ Created provider: {} {} for {}", firstName, lastName, categoryName);

                } catch (Exception e) {
                    logger.warn("❌ Error creating provider {} {}: {}", firstName, lastName, e.getMessage());
                }
            });
        } catch (Exception e) {
            logger.warn("Error in createProviderForCategory: {}", e.getMessage());
        }
    }

    private String generatePhoneNumber() {
        return String.format("(%03d) %03d-%04d",
            (int)(Math.random() * 900) + 100,
            (int)(Math.random() * 900) + 100,
            (int)(Math.random() * 9000) + 1000);
    }

    private String generateAddress() {
        String[] streets = {"Main St", "Oak Ave", "Pine Rd", "Maple Dr", "Cedar Ln", "Elm St"};
        String[] cities = {"Springfield", "Riverside", "Franklin", "Georgetown", "Clinton", "Salem"};

        int number = (int)(Math.random() * 9999) + 1;
        String street = streets[(int)(Math.random() * streets.length)];
        String city = cities[(int)(Math.random() * cities.length)];

        return number + " " + street + ", " + city + ", CA";
    }
}
