package kg.shop.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductForm {

    private Long id;

    @NotBlank(message = "Укажите название")
    @Size(max = 200)
    private String name;

    @NotBlank(message = "Укажите slug (латиница для URL)")
    @Pattern(regexp = "[a-z0-9-]+", message = "Только строчная латиница, цифры и дефис")
    private String slug;

    @Size(max = 2000)
    private String description;

    @NotNull(message = "Укажите цену")
    @DecimalMin(value = "0.01", message = "Цена должна быть больше 0")
    private BigDecimal price;

    private BigDecimal oldPrice;

    @NotNull
    @Min(0)
    private Integer stock = 0;

    @Size(max = 100)
    private String material;

    @NotNull(message = "Выберите категорию")
    private Long categoryId;

    /** URL существующего фото (при редактировании) */
    private String imageUrl;

    /** Только для загрузки нового файла — не привязывается к полям формы */
    // MultipartFile сюда не кладём — принимаем отдельным параметром
}