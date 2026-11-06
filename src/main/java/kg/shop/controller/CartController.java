package kg.shop.controller;

import kg.shop.model.Product;
import kg.shop.repository.ProductRepository;
import kg.shop.service.CartService;
import kg.shop.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final ProductRepository productRepository;
    private final SettingsService settingsService; // ДОБАВЛЕНО

    @GetMapping
    public String view(Model model) {
        var cart = cartService.getCart();
        var settings = settingsService.get();

        BigDecimal totalItems = cart.getTotalPrice();
        BigDecimal deliveryCost = BigDecimal.ZERO;
        BigDecimal freeFrom = BigDecimal.ZERO;
        BigDecimal baseDeliveryPrice = BigDecimal.ZERO;

        try {
            freeFrom = new BigDecimal(settings.getFreeDeliveryFrom());
            baseDeliveryPrice = new BigDecimal(settings.getDeliveryPrice());

            // Если есть товары и сумма меньше порога бесплатной доставки
            if (totalItems.compareTo(BigDecimal.ZERO) > 0 && totalItems.compareTo(freeFrom) < 0) {
                deliveryCost = baseDeliveryPrice;
            }
        } catch (NumberFormatException ignored) {
            // Если в настройках ввели не число, доставка будет 0
        }

        model.addAttribute("cart", cart);
        model.addAttribute("deliveryCost", deliveryCost);
        model.addAttribute("grandTotal", totalItems.add(deliveryCost));
        model.addAttribute("freeFrom", freeFrom);

        return "cart/view";
    }

    // ... остальные методы (add, update, remove) оставляем без изменений ...

    @PostMapping("/add/{id}")
    public String add(@PathVariable Long id, @RequestParam(defaultValue = "1") int quantity,
                      @RequestHeader(value = "Referer", required = false) String referer) {
        Product product = productRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        cartService.getCart().add(product, quantity);
        return "redirect:" + (referer != null ? referer : "/catalog");
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @RequestParam int quantity) {
        cartService.getCart().updateQuantity(id, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/remove/{id}")
    public String remove(@PathVariable Long id) {
        cartService.getCart().remove(id);
        return "redirect:/cart";
    }
}