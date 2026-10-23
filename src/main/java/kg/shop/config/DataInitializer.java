package kg.shop.config;

import kg.shop.model.Category;
import kg.shop.model.Product;
import kg.shop.model.User;
import kg.shop.repository.CategoryRepository;
import kg.shop.repository.ProductRepository;
import kg.shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initAdmin();
        initCatalog();
    }

    private void initAdmin() {
        if (userRepository.findByUsername("admin").isPresent()) return;

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setFullName("Администратор");
        admin.setRole("ROLE_ADMIN");
        userRepository.save(admin);

        System.out.println(">>> Создан администратор: admin / admin123");
    }

    private void initCatalog() {
        if (categoryRepository.count() > 0) return;

        Category plates  = categoryRepository.save(new Category(null, "Тарелки", "plates", 1));
        Category cups    = categoryRepository.save(new Category(null, "Чашки", "cups", 2));
        Category pots    = categoryRepository.save(new Category(null, "Кастрюли", "pots", 3));
        Category cutlery = categoryRepository.save(new Category(null, "Столовые приборы", "cutlery", 4));

        productRepository.save(new Product(null, "Тарелка керамическая белая 27 см",
                "plate-white-27", "Классическая обеденная тарелка из белой керамики. Диаметр 27 см.",
                new BigDecimal("490.00"), null, 50, "керамика",
                "/img/placeholder.svg", plates, null, null));

        productRepository.save(new Product(null, "Тарелка десертная с узором",
                "plate-dessert-pattern", "Десертная тарелка 20 см с ручной росписью.",
                new BigDecimal("380.00"), new BigDecimal("450.00"), 30, "керамика",
                "/img/placeholder.svg", plates, null, null));

        productRepository.save(new Product(null, "Тарелка глубокая для супа",
                "plate-soup", "Глубокая суповая тарелка 22 см.",
                new BigDecimal("550.00"), null, 40, "фарфор",
                "/img/placeholder.svg", plates, null, null));

        productRepository.save(new Product(null, "Чашка чайная с узором",
                "cup-floral", "Фарфоровая чашка 250 мл с цветочным узором.",
                new BigDecimal("350.00"), null, 100, "фарфор",
                "/img/placeholder.svg", cups, null, null));

        productRepository.save(new Product(null, "Кружка керамическая большая",
                "mug-big-ceramic", "Объёмная кружка 400 мл для чая и кофе.",
                new BigDecimal("420.00"), null, 60, "керамика",
                "/img/placeholder.svg", cups, null, null));

        productRepository.save(new Product(null, "Кастрюля чугунная 4 л",
                "pot-cast-4l", "Чугунная кастрюля с толстыми стенками. Равномерный нагрев.",
                new BigDecimal("3290.00"), new BigDecimal("3800.00"), 15, "чугун",
                "/img/placeholder.svg", pots, null, null));

        productRepository.save(new Product(null, "Кастрюля нержавеющая 3 л",
                "pot-steel-3l", "Кастрюля из нержавеющей стали с крышкой.",
                new BigDecimal("1450.00"), null, 25, "нержавейка",
                "/img/placeholder.svg", pots, null, null));

        productRepository.save(new Product(null, "Набор столовых приборов 24 предмета",
                "cutlery-set-24", "Нержавеющая сталь, зеркальная полировка. На 6 персон.",
                new BigDecimal("2190.00"), null, 20, "нержавейка",
                "/img/placeholder.svg", cutlery, null, null));

        productRepository.save(new Product(null, "Ложка чайная, набор 6 шт",
                "tea-spoon-6", "Чайные ложки из нержавеющей стали.",
                new BigDecimal("590.00"), null, 45, "нержавейка",
                "/img/placeholder.svg", cutlery, null, null));

        System.out.println(">>> Инициализация завершена: " +
                categoryRepository.count() + " категорий, " +
                productRepository.count() + " товаров");
    }
}