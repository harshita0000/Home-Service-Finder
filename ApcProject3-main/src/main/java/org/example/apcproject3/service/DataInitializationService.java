package org.example.apcproject3.service;

import org.example.apcproject3.entity.*;
import org.example.apcproject3.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializationService implements CommandLineRunner {

    @Autowired
    private ServiceCategoryService serviceCategoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private ServiceProviderService serviceProviderService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Only initialize if no categories exist
        if (serviceCategoryService.findAllCategories().isEmpty()) {
            initializeCategories();
            initializeProviders();
        }
    }

    private void initializeCategories() {
        // Create service categories
        createCategory("Plumbing", "Professional plumbing services for your home and office", "wrench");
        createCategory("Electrical", "Licensed electricians for all electrical needs", "bolt");
        createCategory("Cleaning", "Professional house and office cleaning services", "broom");
        createCategory("Gardening", "Landscaping and garden maintenance services", "leaf");
        createCategory("Handyman", "General home repair and maintenance services", "hammer");
        createCategory("Painting", "Interior and exterior painting services", "paint-brush");
        createCategory("HVAC", "Heating, ventilation, and air conditioning services", "snowflake");
        createCategory("Roofing", "Roof installation and repair services", "home");
        createCategory("Flooring", "Floor installation and refinishing services", "layer-group");
        createCategory("Carpentry", "Custom woodwork and carpentry services", "cut");
        createCategory("Pest Control", "Professional pest control and extermination", "bug");
        createCategory("Security", "Home and office security system installation", "shield-alt");
    }

    private void createCategory(String name, String description, String icon) {
        ServiceCategory category = new ServiceCategory();
        category.setName(name);
        category.setDescription(description);
        category.setIcon(icon);
        category.setActive(true);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        serviceCategoryService.createCategory(category);
    }

    private void initializeProviders() {
        List<ServiceCategory> categories = serviceCategoryService.findAllCategories();

        for (ServiceCategory category : categories) {
            createProvidersForCategory(category);
        }
    }

    private void createProvidersForCategory(ServiceCategory category) {
        String categoryName = category.getName().toLowerCase();

        switch (categoryName) {
            case "plumbing":
                createProvider("John", "Smith", "johnsmith", "john.smith@example.com", category,
                    "Experienced plumber with 10+ years in residential and commercial plumbing", 45.0, 10, 4.8f);
                createProvider("Mike", "Johnson", "mikejohnson", "mike.johnson@example.com", category,
                    "Licensed master plumber specializing in emergency repairs", 55.0, 15, 4.9f);
                createProvider("Sarah", "Williams", "sarahwilliams", "sarah.williams@example.com", category,
                    "Expert in modern plumbing systems and eco-friendly solutions", 40.0, 8, 4.7f);
                createProvider("David", "Brown", "davidbrown", "david.brown@example.com", category,
                    "Reliable plumber with expertise in pipe installation and repair", 42.0, 12, 4.6f);
                createProvider("Lisa", "Davis", "lisadavis", "lisa.davis@example.com", category,
                    "Professional plumber with focus on bathroom and kitchen remodeling", 48.0, 9, 4.8f);
                createProvider("Tom", "Wilson", "tomwilson", "tom.wilson@example.com", category,
                    "24/7 emergency plumber with quick response time", 50.0, 7, 4.5f);
                // Additional plumbers
                createProvider("Robert", "Taylor", "roberttaylor", "robert.taylor@example.com", category,
                    "Specialized in sewer line repair and drain cleaning services", 46.0, 11, 4.7f);
                createProvider("Amanda", "Moore", "amandamoore", "amanda.moore@example.com", category,
                    "Water heater installation and repair specialist", 44.0, 9, 4.6f);
                createProvider("Chris", "Jackson", "chrisjackson", "chris.jackson@example.com", category,
                    "Commercial plumbing contractor with industrial experience", 58.0, 16, 4.8f);
                break;

            case "electrical":
                createProvider("Alex", "Miller", "alexmiller", "alex.miller@example.com", category,
                    "Licensed electrician with expertise in home automation systems", 60.0, 12, 4.9f);
                createProvider("Emma", "Taylor", "emmataylor", "emma.taylor@example.com", category,
                    "Certified electrical contractor for residential and commercial projects", 55.0, 15, 4.8f);
                createProvider("James", "Anderson", "jamesanderson", "james.anderson@example.com", category,
                    "Expert in electrical panel upgrades and wiring installation", 52.0, 10, 4.7f);
                createProvider("Maria", "Garcia", "mariagarcia", "maria.garcia@example.com", category,
                    "Specialized in LED lighting and energy-efficient electrical solutions", 48.0, 8, 4.6f);
                createProvider("Robert", "Martinez", "robertmartinez", "robert.martinez@example.com", category,
                    "Emergency electrician available 24/7 for urgent electrical issues", 65.0, 18, 4.9f);
                createProvider("Jennifer", "Lee", "jenniferlee", "jennifer.lee@example.com", category,
                    "Smart home electrical specialist with modern technology expertise", 58.0, 6, 4.5f);
                // Additional electricians
                createProvider("Michael", "Thompson", "michaelthompson", "michael.thompson@example.com", category,
                    "Industrial electrical systems and motor control specialist", 62.0, 14, 4.8f);
                createProvider("Jessica", "White", "jessicawhite", "jessica.white@example.com", category,
                    "Solar panel installation and renewable energy expert", 56.0, 7, 4.7f);
                createProvider("Daniel", "Harris", "danielharris", "daniel.harris@example.com", category,
                    "Electrical troubleshooting and code compliance specialist", 50.0, 13, 4.6f);
                break;

            case "cleaning":
                createProvider("Anna", "Thompson", "annathompson", "anna.thompson@example.com", category,
                    "Professional house cleaner with eco-friendly products", 25.0, 5, 4.8f);
                createProvider("Carlos", "Rodriguez", "carlosrodriguez", "carlos.rodriguez@example.com", category,
                    "Commercial and residential cleaning specialist", 28.0, 8, 4.7f);
                createProvider("Sophie", "White", "sophiewhite", "sophie.white@example.com", category,
                    "Deep cleaning expert with attention to detail", 30.0, 6, 4.9f);
                createProvider("Mark", "Harris", "markharris", "mark.harris@example.com", category,
                    "Move-in/move-out cleaning specialist", 26.0, 4, 4.6f);
                createProvider("Linda", "Clark", "lindaclark", "linda.clark@example.com", category,
                    "Office and home cleaning with flexible scheduling", 24.0, 7, 4.5f);
                createProvider("Daniel", "Lewis", "daniellewis", "daniel.lewis@example.com", category,
                    "Post-construction cleanup and regular maintenance cleaning", 32.0, 9, 4.8f);
                // Additional cleaners
                createProvider("Isabella", "Robinson", "isabellarobinson", "isabella.robinson@example.com", category,
                    "Carpet and upholstery cleaning specialist", 35.0, 10, 4.7f);
                createProvider("Anthony", "Walker", "anthonywalker", "anthony.walker@example.com", category,
                    "Window cleaning and exterior building maintenance", 28.0, 6, 4.6f);
                createProvider("Rachel", "Young", "rachelyoung", "rachel.young@example.com", category,
                    "Sanitization and disinfection services specialist", 30.0, 5, 4.8f);
                break;

            case "gardening":
                createProvider("Grace", "Walker", "gracewalker", "grace.walker@example.com", category,
                    "Landscape designer with expertise in sustainable gardening", 35.0, 12, 4.9f);
                createProvider("Peter", "Hall", "peterhall", "peter.hall@example.com", category,
                    "Professional gardener specializing in lawn care and maintenance", 30.0, 15, 4.7f);
                createProvider("Amy", "Young", "amyyoung", "amy.young@example.com", category,
                    "Organic gardening specialist with plant care expertise", 32.0, 8, 4.8f);
                createProvider("Steve", "Allen", "steveallen", "steve.allen@example.com", category,
                    "Tree service and landscaping professional", 38.0, 20, 4.6f);
                createProvider("Rachel", "King", "rachelking", "rachel.king@example.com", category,
                    "Garden design and seasonal planting specialist", 33.0, 6, 4.5f);
                createProvider("Kevin", "Wright", "kevinwright", "kevin.wright@example.com", category,
                    "Irrigation systems and water-efficient landscaping expert", 40.0, 10, 4.8f);
                // Additional gardeners
                createProvider("Benjamin", "Lopez", "benjaminlopez", "benjamin.lopez@example.com", category,
                    "Fruit tree care and vegetable garden specialist", 34.0, 9, 4.7f);
                createProvider("Olivia", "Hill", "oliviahill", "olivia.hill@example.com", category,
                    "Native plant landscaping and drought-resistant gardens", 36.0, 7, 4.6f);
                createProvider("Tyler", "Scott", "tylerscott", "tyler.scott@example.com", category,
                    "Hardscaping and outdoor living space design", 42.0, 11, 4.8f);
                break;

            case "handyman":
                createProvider("Chris", "Green", "chrisgreen", "chris.green@example.com", category,
                    "Multi-skilled handyman for all home repair needs", 35.0, 10, 4.7f);
                createProvider("Jessica", "Adams", "jessicaadams", "jessica.adams@example.com", category,
                    "Furniture assembly and home improvement specialist", 32.0, 6, 4.8f);
                createProvider("Brian", "Baker", "brianbaker", "brian.baker@example.com", category,
                    "Drywall repair and painting touch-up expert", 30.0, 8, 4.6f);
                createProvider("Michelle", "Nelson", "michellenelson", "michelle.nelson@example.com", category,
                    "Small home repairs and maintenance specialist", 28.0, 5, 4.5f);
                createProvider("Eric", "Carter", "ericcarter", "eric.carter@example.com", category,
                    "Carpentry and woodwork handyman services", 42.0, 15, 4.9f);
                createProvider("Nicole", "Mitchell", "nicolemitchell", "nicole.mitchell@example.com", category,
                    "Home organization and minor repair specialist", 26.0, 4, 4.4f);
                // Additional handymen
                createProvider("Jonathan", "Adams", "jonathanadams", "jonathan.adams@example.com", category,
                    "Appliance repair and installation expert", 38.0, 12, 4.7f);
                createProvider("Samantha", "Collins", "samanthacollins", "samantha.collins@example.com", category,
                    "Tile and flooring repair specialist", 34.0, 8, 4.6f);
                createProvider("Marcus", "Evans", "marcusevans", "marcus.evans@example.com", category,
                    "Deck and fence repair and maintenance", 36.0, 9, 4.8f);
                break;

            case "painting":
                createProvider("Andrew", "Perez", "andrewperez", "andrew.perez@example.com", category,
                    "Interior and exterior painting contractor", 40.0, 12, 4.8f);
                createProvider("Laura", "Roberts", "lauraroberts", "laura.roberts@example.com", category,
                    "Decorative painting and wall finishing expert", 45.0, 8, 4.9f);
                createProvider("Marcus", "Turner", "marcusturner", "marcus.turner@example.com", category,
                    "Commercial and residential painting services", 38.0, 15, 4.7f);
                createProvider("Kelly", "Phillips", "kellyphillips", "kelly.phillips@example.com", category,
                    "Color consultation and premium painting services", 42.0, 6, 4.6f);
                createProvider("Ryan", "Campbell", "ryancampbell", "ryan.campbell@example.com", category,
                    "Quick and efficient painting for time-sensitive projects", 36.0, 9, 4.5f);
                createProvider("Stephanie", "Parker", "stephanieparker", "stephanie.parker@example.com", category,
                    "Eco-friendly paint specialist with health-conscious solutions", 44.0, 7, 4.8f);
                // Additional painters
                createProvider("William", "Morris", "williammorris", "william.morris@example.com", category,
                    "Cabinet refinishing and furniture painting specialist", 41.0, 10, 4.7f);
                createProvider("Victoria", "Cooper", "victoriacooper", "victoria.cooper@example.com", category,
                    "Mural and artistic wall painting services", 48.0, 5, 4.6f);
                createProvider("Nathan", "Reed", "nathanreed", "nathan.reed@example.com", category,
                    "Pressure washing and exterior surface preparation", 33.0, 11, 4.8f);
                break;

            // Add new service categories with providers
            case "hvac":
                createProvider("Thomas", "Anderson", "thomasanderson", "thomas.anderson@example.com", category,
                    "HVAC installation and repair specialist with 15+ years experience", 58.0, 15, 4.8f);
                createProvider("Patricia", "Brown", "patriciabrown", "patricia.brown@example.com", category,
                    "Energy-efficient heating and cooling system expert", 52.0, 12, 4.7f);
                createProvider("Richard", "Davis", "richarddavis", "richard.davis@example.com", category,
                    "Commercial HVAC systems and maintenance specialist", 62.0, 18, 4.9f);
                createProvider("Jennifer", "Wilson", "jenniferwilson", "jennifer.wilson@example.com", category,
                    "Ductwork installation and air quality improvement expert", 48.0, 9, 4.6f);
                createProvider("Charles", "Miller", "charlesmiller", "charles.miller@example.com", category,
                    "Emergency HVAC repairs and 24/7 service availability", 55.0, 13, 4.8f);
                createProvider("Lisa", "Moore", "lisamoore", "lisa.moore@example.com", category,
                    "Heat pump and geothermal system installation specialist", 60.0, 11, 4.7f);
                break;

            case "roofing":
                createProvider("Michael", "Johnson", "michaeljohnson2", "michael.johnson2@example.com", category,
                    "Residential and commercial roofing contractor", 65.0, 20, 4.9f);
                createProvider("Sarah", "Thompson", "sarahthompson2", "sarah.thompson2@example.com", category,
                    "Roof repair and emergency leak fixing specialist", 55.0, 14, 4.8f);
                createProvider("David", "Garcia", "davidgarcia", "david.garcia@example.com", category,
                    "Metal roofing and solar panel integration expert", 60.0, 16, 4.7f);
                createProvider("Karen", "Martinez", "karenmartinez", "karen.martinez@example.com", category,
                    "Gutter installation and roof maintenance specialist", 50.0, 10, 4.6f);
                createProvider("James", "Rodriguez", "jamesrodriguez", "james.rodriguez@example.com", category,
                    "Storm damage repair and insurance claim specialist", 58.0, 17, 4.8f);
                createProvider("Michelle", "Lopez", "michellelopez", "michelle.lopez@example.com", category,
                    "Green roofing and sustainable roofing solutions", 62.0, 8, 4.5f);
                break;

            case "flooring":
                createProvider("Nancy", "Wilson", "nancywilson", "nancy.wilson@example.com", category,
                    "Hardwood and laminate floor installation expert", 30.0, 10, 4.8f);
                createProvider("Kevin", "Moore", "kevinmoore", "kevin.moore@example.com", category,
                    "Tile and stone flooring specialist", 35.0, 12, 4.7f);
                createProvider("Laura", "Taylor", "laurataylor", "laura.taylor@example.com", category,
                    "Vinyl and carpet flooring installation and repair", 28.0, 8, 4.6f);
                createProvider("James", "Anderson", "jamesanderson2", "james.anderson2@example.com", category,
                    "Floor refinishing and restoration expert", 40.0, 15, 4.9f);
                createProvider("Patricia", "Clark", "patriciaclark", "patricia.clark@example.com", category,
                    "Luxury vinyl plank and engineered wood specialist", 32.0, 7, 4.5f);
                createProvider("Timothy", "Lewis", "timothylewis", "timothy.lewis@example.com", category,
                    "Commercial flooring and industrial floor coatings", 45.0, 18, 4.7f);
                createProvider("Susan", "Hall", "susanhall", "susan.hall@example.com", category,
                    "Bamboo and eco-friendly flooring solutions", 38.0, 9, 4.8f);
                createProvider("Frank", "Wright", "frankwright", "frank.wright@example.com", category,
                    "Subfloor repair and moisture damage restoration", 42.0, 13, 4.6f);
                break;

            case "carpentry":
                createProvider("Matthew", "King", "matthewking", "matthew.king@example.com", category,
                    "Custom cabinet maker and kitchen renovation specialist", 50.0, 16, 4.9f);
                createProvider("Deborah", "Scott", "deborahscott", "deborah.scott@example.com", category,
                    "Fine furniture craftsman and antique restoration", 55.0, 20, 4.8f);
                createProvider("Joseph", "Green", "josephgreen", "joseph.green@example.com", category,
                    "Trim work and crown molding installation expert", 38.0, 11, 4.7f);
                createProvider("Carol", "Adams", "caroladams", "carol.adams@example.com", category,
                    "Built-in shelving and custom storage solutions", 42.0, 9, 4.6f);
                createProvider("Donald", "Baker", "donaldbaker", "donald.baker@example.com", category,
                    "Deck building and outdoor structure specialist", 45.0, 14, 4.8f);
                createProvider("Helen", "Gonzalez", "helengonzalez", "helen.gonzalez@example.com", category,
                    "Staircase construction and handrail installation", 48.0, 12, 4.5f);
                createProvider("Gary", "Nelson", "garynelson", "gary.nelson@example.com", category,
                    "Door and window frame carpentry specialist", 40.0, 10, 4.7f);
                createProvider("Betty", "Carter", "bettycarter", "betty.carter@example.com", category,
                    "Custom closet and wardrobe design specialist", 52.0, 8, 4.8f);
                break;

            case "pest control":
                createProvider("Steven", "Mitchell", "stevenmitchell", "steven.mitchell@example.com", category,
                    "Licensed pest control technician with eco-friendly methods", 35.0, 8, 4.7f);
                createProvider("Dorothy", "Perez", "dorothyperez", "dorothy.perez@example.com", category,
                    "Termite inspection and treatment specialist", 42.0, 12, 4.8f);
                createProvider("Paul", "Roberts", "paulroberts", "paul.roberts@example.com", category,
                    "Rodent control and wildlife management expert", 38.0, 10, 4.6f);
                createProvider("Sandra", "Turner", "sandraturner", "sandra.turner@example.com", category,
                    "Integrated pest management for commercial properties", 45.0, 15, 4.9f);
                createProvider("Kenneth", "Phillips", "kennethphillips", "kenneth.phillips@example.com", category,
                    "Bed bug and indoor pest elimination specialist", 40.0, 7, 4.5f);
                createProvider("Lisa", "Campbell", "lisacampbell", "lisa.campbell@example.com", category,
                    "Organic and non-toxic pest control solutions", 36.0, 6, 4.7f);
                createProvider("Mark", "Parker", "markparker", "mark.parker@example.com", category,
                    "Ant and insect control with preventive treatments", 33.0, 9, 4.6f);
                createProvider("Ruth", "Evans", "ruthevans", "ruth.evans@example.com", category,
                    "Mosquito control and outdoor pest management", 37.0, 11, 4.8f);
                break;

            case "security":
                createProvider("Edward", "Edwards", "edwardedwards", "edward.edwards@example.com", category,
                    "Smart home security system installation expert", 55.0, 12, 4.8f);
                createProvider("Sharon", "Collins", "sharoncollins", "sharon.collins@example.com", category,
                    "CCTV and surveillance system specialist", 50.0, 10, 4.7f);
                createProvider("George", "Stewart", "georgestewart", "george.stewart@example.com", category,
                    "Access control and keyless entry system expert", 48.0, 15, 4.9f);
                createProvider("Cynthia", "Sanchez", "cynthiasanchez", "cynthia.sanchez@example.com", category,
                    "Alarm system installation and monitoring setup", 45.0, 8, 4.6f);
                createProvider("Ronald", "Morris", "ronaldmorris", "ronald.morris@example.com", category,
                    "Commercial security and fire safety system specialist", 60.0, 18, 4.8f);
                createProvider("Donna", "Rogers", "donnarogers", "donna.rogers@example.com", category,
                    "Home automation and smart security integration", 52.0, 7, 4.5f);
                createProvider("Jason", "Reed", "jasonreed", "jason.reed@example.com", category,
                    "Safe installation and security consultation", 58.0, 14, 4.7f);
                createProvider("Angela", "Cook", "angelacook", "angela.cook@example.com", category,
                    "Wireless security system and remote monitoring", 46.0, 6, 4.6f);
                break;
        }
    }

    private void createProvider(String firstName, String lastName, String username, String email,
                              ServiceCategory category, String bio, double hourlyRate, int experience, float rating) {
        try {
            // Check if user already exists to avoid duplicates
            if (userService.existsByUsername(username) || userService.existsByEmail(email)) {
                System.out.println("Skipping duplicate provider: " + firstName + " " + lastName);
                return;
            }

            // Create user account
            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode("password123")); // Default password
            user.setRole(UserRole.SERVICE_PROVIDER);
            user.setEnabled(true);
            user.setPhoneNumber(generatePhoneNumber());
            user.setAddress(generateAddress());
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            User savedUser = userService.createUser(user);

            // Create service provider profile
            ServiceProvider provider = new ServiceProvider();
            provider.setUser(savedUser);
            provider.setCategory(category);
            provider.setBio(bio);
            provider.setHourlyRate(BigDecimal.valueOf(hourlyRate));
            provider.setExperienceYears(BigDecimal.valueOf(experience));
            provider.setRating(BigDecimal.valueOf(rating));
            provider.setTotalReviews((int) (Math.random() * 50) + 5); // Random reviews between 5-55
            provider.setAvailable(Math.random() > 0.2); // 80% chance of being available
            provider.setVerified(Math.random() > 0.3); // 70% chance of being verified
            provider.setCreatedAt(LocalDateTime.now());
            provider.setUpdatedAt(LocalDateTime.now());

            serviceProviderService.createProvider(provider);

            System.out.println("✅ Created provider: " + firstName + " " + lastName + " (" + category.getName() + ")");

        } catch (Exception e) {
            System.err.println("❌ Error creating provider " + firstName + " " + lastName + ": " + e.getMessage());
        }
    }

    private String generatePhoneNumber() {
        return String.format("(%03d) %03d-%04d",
            (int)(Math.random() * 900) + 100,
            (int)(Math.random() * 900) + 100,
            (int)(Math.random() * 9000) + 1000);
    }

    private String generateAddress() {
        String[] streets = {"Main St", "Oak Ave", "Pine Rd", "Maple Dr", "Cedar Ln", "Elm St",
                           "First St", "Second Ave", "Park Blvd", "Washington St", "Lincoln Ave", "Broadway"};
        String[] cities = {"Springfield", "Riverside", "Franklin", "Georgetown", "Clinton", "Salem",
                          "Madison", "Oakland", "Bristol", "Manchester", "Ashland", "Fairview"};

        int number = (int)(Math.random() * 9999) + 1;
        String street = streets[(int)(Math.random() * streets.length)];
        String city = cities[(int)(Math.random() * cities.length)];

        return number + " " + street + ", " + city + ", CA";
    }
}
