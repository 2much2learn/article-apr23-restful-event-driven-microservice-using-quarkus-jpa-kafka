package com.toomuch2learn.crud.catalogue.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPrice {

    private String skuNumber;
    private Double price;

}
