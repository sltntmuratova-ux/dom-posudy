package kg.shop.controller;

import kg.shop.model.Order;
import kg.shop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/track")
@RequiredArgsConstructor
public class OrderTrackingController {

    private final OrderRepository orderRepository;

    @GetMapping
    public String form() {
        return "track/index";
    }

    @PostMapping
    public String search(@RequestParam("phone") String phone, Model model) {
        // Очищаем номер от пробелов и дефисов для точного поиска
        String cleanPhone = phone.replaceAll("[^0-9+]", "");

        List<Order> orders = orderRepository.findByCustomerPhoneOrderByCreatedAtDesc(cleanPhone);

        model.addAttribute("orders", orders);
        model.addAttribute("phone", phone);

        return "track/index";
    }
}