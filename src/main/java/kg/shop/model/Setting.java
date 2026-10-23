package kg.shop.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Setting {

    @Id
    @Column(length = 100)
    private String key;

    @Column(length = 2000)
    private String value;
}