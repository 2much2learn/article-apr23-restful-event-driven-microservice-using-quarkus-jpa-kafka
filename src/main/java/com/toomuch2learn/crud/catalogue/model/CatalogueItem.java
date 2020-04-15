package com.toomuch2learn.crud.catalogue.model;

import com.toomuch2learn.crud.catalogue.validation.IEnumValidator;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor(staticName = "of")
@Entity
@Table(name = "CATALOGUE_ITEMS",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "SKU_NUMBER")
})
public class CatalogueItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @NotEmpty(message = "SKU cannot be null or empty")
    @NonNull
    @Column(name = "SKU_NUMBER", unique = true, nullable = false, length = 16)
    private String sku;

    @NotEmpty(message = "Name cannot be null or empty")
    @NonNull
    @Column(name = "ITEM_NAME", unique = true, nullable = false, length = 255)
    private String name;

    @NotEmpty(message = "Description cannot be null or empty")
    @NonNull
    @Column(name = "DESCRIPTION", nullable = false, length = 500)
    private String description;

    @NonNull
    @Column(name = "CATEGORY", nullable = false)
    @IEnumValidator(
        enumClazz = Category.class,
        message = "Invalid category provided"
    )
    private String category;

    @NotNull(message = "Price cannot be null or empty")
    @NonNull
    @Column(name = "PRICE", nullable = false, precision = 10, scale = 2)
    private Double price;

    @NotNull(message = "Inventory cannot be null or empty")
    @NonNull
    @Column(name = "INVENTORY", nullable = false)
    private Integer inventory;

    @NonNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_ON", nullable = false, length = 19)
    private Date createdOn;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_ON", nullable = true, length = 19)
    private Date updatedOn;
}
