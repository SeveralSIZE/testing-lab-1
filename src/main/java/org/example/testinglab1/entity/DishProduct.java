package org.example.testinglab1.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "dish_products")
@Getter
@Setter
@NoArgsConstructor
public class DishProduct {
    @EmbeddedId
    private DishProductId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("dishId")
    @JoinColumn(name = "dish_id")
    private Dish dish;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Double amount;

    @Embeddable
    public static class DishProductId implements Serializable {

        @Column(name = "dish_id")
        private UUID dishId;

        @Column(name = "product_id")
        private UUID productId;

        public DishProductId() {}

        public DishProductId(UUID dishId, UUID productId) {
            this.dishId = dishId;
            this.productId = productId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DishProductId that)) return false;
            return Objects.equals(dishId, that.dishId) &&
                    Objects.equals(productId, that.productId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dishId, productId);
        }
    }
}
