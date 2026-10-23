package kg.shop.cart;

import kg.shop.model.Product;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class Cart {

    private final List<CartItem> items = new ArrayList<>();

    /** Добавить товар в корзину или увеличить количество, если уже есть */
    public void add(Product product, int quantity) {
        Optional<CartItem> existing = items.stream()
                .filter(i -> i.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + quantity);
        } else {
            items.add(new CartItem(product, quantity));
        }
    }

    /** Изменить количество конкретной позиции */
    public void updateQuantity(Long productId, int quantity) {
        if (quantity <= 0) {
            remove(productId);
            return;
        }
        items.stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(i -> i.setQuantity(quantity));
    }

    /** Удалить позицию */
    public void remove(Long productId) {
        items.removeIf(i -> i.getProduct().getId().equals(productId));
    }

    /** Очистить корзину */
    public void clear() {
        items.clear();
    }

    /** Общее количество единиц товаров (для счётчика в шапке) */
    public int getTotalItems() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    /** Итоговая сумма корзины */
    public BigDecimal getTotalPrice() {
        return items.stream()
                .map(CartItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Пустая ли корзина */
    public boolean isEmpty() {
        return items.isEmpty();
    }
}