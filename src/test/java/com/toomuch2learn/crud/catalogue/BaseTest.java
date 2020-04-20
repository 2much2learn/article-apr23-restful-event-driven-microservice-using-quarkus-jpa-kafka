package com.toomuch2learn.crud.catalogue;

import com.toomuch2learn.crud.catalogue.model.CatalogueItem;
import com.toomuch2learn.crud.catalogue.model.Category;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import java.util.Date;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class BaseTest {

    final Random random = new Random();
    String prepareRandomSKUNumber() {
        return "SKUNUMBER-"+
            random.ints(1000, 9999)
                .findFirst()
                .getAsInt();
    }

    ResponseSpecification prepareResponseSpec(int responseStatus) {
        return new ResponseSpecBuilder()
            .expectStatusCode(responseStatus)
            .build();
    }

    Response postCreateCatalogueItem(CatalogueItem catalogueItem) throws Exception {
        RequestSpecification request
            = given()
                .contentType("application/json")
                .body(catalogueItem);

        return request.post("/");
    }

    CatalogueItem prepareCatalogueItem(String skuNumber) {
        CatalogueItem item
            = CatalogueItem.of(
                skuNumber,
                "Catalog Item -"+skuNumber,
                "Catalog Desc - "+skuNumber,
                Category.BOOKS.getValue(),
                10.00,
                10,
                new Date()
            );
        return item;
    }
}
