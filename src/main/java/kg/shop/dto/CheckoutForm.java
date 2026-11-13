package kg.shop.dto;

import kg.shop.model.DeliveryMethod;
import kg.shop.model.PaymentMethod;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutForm {

    @NotBlank(message = "Укажите имя")
    @Size(min = 2, max = 150)
    private String name;

    @NotBlank(message = "Укажите телефон")
    private String phone;

    @Email(message = "Некорректный email")
    private String email;

    @NotBlank(message = "Укажите город")
    private String city;

    @NotBlank(message = "Укажите адрес")
    private String address;

    @NotNull
    private DeliveryMethod deliveryMethod = DeliveryMethod.COURIER;

    @NotNull
    private PaymentMethod paymentMethod = PaymentMethod.CASH;

    @Size(max = 1000)
    private String comment;

    // НОВОЕ ПОЛЕ: Согласие на обработку данных
    @AssertTrue(message = "Необходимо согласиться с политикой конфиденциальности")
    private boolean agreement = false;
}