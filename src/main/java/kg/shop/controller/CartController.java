package kg.shop.controller;

import kg.shop.model.Product;
import kg.shop.repository.ProductRepository;
import kg.shop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final ProductRepository productRepository;

    /** Страница корзины */
    @GetMapping
    public String view(Model model) {
        model.addAttribute("cart", cartService.getCart());
        return "cart/view";
    }

    /** Добавить товар в корзину */
    @PostMapping("/add/{id}")
    public String add(@PathVariable Long id,
                      @RequestParam(defaultValue = "1") int quantity,
                      @RequestHeader(value = "Referer", required = false) String referer) {
        Product product = productRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        cartService.getCart().add(product, quantity);

        // Возвращаем пользователя туда, откуда пришёл (карточка товара или каталог)
        return "redirect:" + (referer != null ? referer : "/catalog");
    }

    /** Изменить количество */
    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @RequestParam int quantity) {
        cartService.getCart().updateQuantity(id, quantity);
        return "redirect:/cart";
    }

    /** Удалить позицию */
    @PostMapping("/remove/{id}")
    public String remove(@PathVariable Long id) {
        cartService.getCart().remove(id);
        return "redirect:/cart";
    }
}