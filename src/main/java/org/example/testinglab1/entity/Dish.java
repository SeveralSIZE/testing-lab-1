package org.example.testinglab1.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.testinglab1.enums.DishCategory;
import org.example.testinglab1.enums.Flag;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "dishes")
@Getter
@Setter
@NoArgsConstructor
public class Dish {

    @Id
    @Column(name = "id")
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @ElementCollection
    @CollectionTable(name = "dish_photos", joinColumns = @JoinColumn(name = "dish_id"))
    @Column(name = "photo_url")
    private List<String> photos = new ArrayList<>();

    @Column(name = "calories", nullable = false)
    private Double calories;

    @Column(name = "proteins", nullable = false)
    private Double proteins;

    @Column(name = "fats", nullable = false)
    private Double fats;

    @Column(name = "carbohydrates", nullable = false)
    private Double carbohydrates;

    @Column(name = "portion_size", nullable = false)
    private Double portionSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private DishCategory category;

    @ElementCollection
    @CollectionTable(name = "dish_flag", joinColumns = @JoinColumn(name = "dish_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "flag")
    private Set<Flag> flags = new HashSet<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "dish", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DishProduct> ingredients = new ArrayList<>();

    @PrePersist
    private void prePersist() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
