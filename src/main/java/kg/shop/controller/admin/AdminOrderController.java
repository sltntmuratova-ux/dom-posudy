package kg.shop.controller.admin;

import kg.shop.model.Order;
import kg.shop.model.OrderStatus;
import kg.shop.repository.OrderRepository;
import kg.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("orders", orderRepository.findAllWithItems());
        return "admin/orders";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        // Инициализируем items и продукт
        order.getItems().forEach(i -> i.getProductName());
        model.addAttribute("order", order);
        model.addAttribute("statuses", OrderStatus.values());
        return "admin/order-detail";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam OrderStatus status,
                               RedirectAttributes ra) {
        orderService.updateStatus(id, status);
        ra.addFlashAttribute("success", "Статус обновлён");
        return "redirect:/admin/orders/" + id;
    }
}