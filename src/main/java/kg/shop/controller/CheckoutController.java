package kg.shop.controller;

import jakarta.validation.Valid;
import kg.shop.dto.CheckoutForm;
import kg.shop.model.Order;
import kg.shop.service.CartService;
import kg.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CartService cartService;
    private final OrderService orderService;
    private final kg.shop.service.SettingsService settingsService;

    private java.util.List<String> cities() {
        String raw = settingsService.get().getDeliveryCities();
        if (raw == null || raw.isBlank()) return java.util.List.of();
        return java.util.Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    @GetMapping
    public String form(Model model) {
        if (cartService.getCart().isEmpty()) {
            return "redirect:/cart";
        }
        model.addAttribute("form", new CheckoutForm());
        model.addAttribute("cart", cartService.getCart());
        return "checkout/form";
    }

    @PostMapping
    public String submit(@Valid @ModelAttribute("form") CheckoutForm form,
                         BindingResult result,
                         Model model) {
        if (cartService.getCart().isEmpty()) {
            return "redirect:/cart";
        }
        if (result.hasErrors()) {
            model.addAttribute("cart", cartService.getCart());
            return "checkout/form";
        }

        Order order = orderService.createOrder(cartService.getCart(), form);
        cartService.getCart().clear();

        return "redirect:/checkout/success/" + order.getNumber();
    }

    @GetMapping("/success/{number}")
    public String success(@PathVariable String number, Model model) {
        Order order = orderService.findByNumber(number);
        if (order == null) return "redirect:/catalog";
        model.addAttribute("order", order);
        return "checkout/success";
    }
}