package kg.shop.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(nullable = false, length = 20)
    private String type = "IMAGE"; // IMAGE или VIDEO

    @Column(name = "sort_order")
    private Integer sortOrder = 0;
}