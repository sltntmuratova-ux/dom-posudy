package kg.shop.cart;

import kg.shop.model.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CartItem {

    private Product product;
    private int quantity;

    public java.math.BigDecimal getTotal() {
        return product.getPrice().multiply(java.math.BigDecimal.valueOf(quantity));
    }
}