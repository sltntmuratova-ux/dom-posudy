package kg.shop.controller.admin;

import kg.shop.model.OrderStatus;
import kg.shop.repository.CategoryRepository;
import kg.shop.repository.OrderRepository;
import kg.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("productCount", productRepository.count());
        model.addAttribute("categoryCount", categoryRepository.count());
        model.addAttribute("orderCount", orderRepository.count());
        model.addAttribute("newOrders",
                orderRepository.findAll().stream()
                        .filter(o -> o.getStatus() == OrderStatus.NEW)
                        .count());
        return "admin/dashboard";
    }

    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("products", productRepository.findAllWithCategory());
        return "admin/products";
    }
}