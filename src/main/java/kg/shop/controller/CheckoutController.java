package kg.shop.controller;

import jakarta.validation.Valid;
import kg.shop.dto.CheckoutForm;
import kg.shop.model.Order;
import kg.shop.service.CartService;
import kg.shop.service.OrderService;
import kg.shop.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CartService cartService;
    private final OrderService orderService;
    private final SettingsService settingsService;

    private List<String> cities() {
        String raw = settingsService.get().getDeliveryCities();
        if (raw == null || raw.isBlank()) return List.of();
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    @GetMapping
    public String form(Model model) {
        if (cartService.getCart().isEmpty()) {
            return "redirect:/cart";
        }

        // Расчет стоимости доставки для отображения на странице
        BigDecimal totalItems = cartService.getCart().getTotalPrice();
        BigDecimal deliveryCost = BigDecimal.ZERO;

        try {
            BigDecimal freeFrom = new BigDecimal(settingsService.get().getFreeDeliveryFrom());
            BigDecimal basePrice = new BigDecimal(settingsService.get().getDeliveryPrice());

            if (totalItems.compareTo(freeFrom) < 0) {
                deliveryCost = basePrice;
            }
        } catch (Exception ignored) {
            // Если в настройках некорректные числа, доставка будет 0
        }

        model.addAttribute("form", new CheckoutForm());
        model.addAttribute("cart", cartService.getCart());
        model.addAttribute("cities", cities()); // ВАЖНО: возвращаем список городов
        model.addAttribute("deliveryCost", deliveryCost);
        model.addAttribute("grandTotal", totalItems.add(deliveryCost));

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
            // Если есть ошибки валидации, возвращаем на форму со всеми данными
            BigDecimal totalItems = cartService.getCart().getTotalPrice();
            BigDecimal deliveryCost = BigDecimal.ZERO;
            try {
                BigDecimal freeFrom = new BigDecimal(settingsService.get().getFreeDeliveryFrom());
                BigDecimal basePrice = new BigDecimal(settingsService.get().getDeliveryPrice());
                if (totalItems.compareTo(freeFrom) < 0) deliveryCost = basePrice;
            } catch (Exception ignored) {}

            model.addAttribute("cart", cartService.getCart());
            model.addAttribute("cities", cities());
            model.addAttribute("deliveryCost", deliveryCost);
            model.addAttribute("grandTotal", totalItems.add(deliveryCost));
            return "checkout/form";
        }

        // Создаем заказ (OrderService сам посчитает доставку и прибавит её к сумме)
        Order order = orderService.createOrder(cartService.getCart(), form);

        // Очищаем корзину
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