package com.toomuch2learn.crud.catalogue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.toomuch2learn.crud.catalogue.config.KafkaResource;
import com.toomuch2learn.crud.catalogue.error.Error;
import com.toomuch2learn.crud.catalogue.model.CatalogueItem;
import com.toomuch2learn.crud.catalogue.model.Category;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.DisabledOnNativeImage;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

@QuarkusTest
@QuarkusTestResource(KafkaResource.class)
@DisabledOnNativeImage
public class RestAssuredCatalogueCRUDTest {

    @BeforeEach
    public void setURL() {
        RestAssured.baseURI = "http://[::1]:8081/api/v1";
    }

    @Test
    @DisplayName("Test if Application is up by accessing health endpoint")
    public void test_applicationIsUp() {
        try {
            given()
                .get("http://[::1]:8081/health")
            .then()
                .assertThat().spec(prepareResponseSpec(200))
            .and()
                .assertThat().body("status", equalTo("UP"));
        }
        catch(Exception e) {
            fail("Error occurred while tesing application health check", e);
        }
    }

    @Test
    @DisplayName("Test Create Catalogue Item")
    public void test_createCatalogueItem() {
        try {
            postCreateCatalogueItem(prepareCatalogueItem(prepareRandomSKUNumber()))
            .then()
                .assertThat().spec(prepareResponseSpec(201))
            .and()
                .assertThat().body("id", greaterThan(0));
        }
        catch(Exception e) {
            fail("Error occurred while testing catalogue item create endpoint", e);
        }
    }

    @Test
    @DisplayName("Test Get Catalogue Items")
    public void test_getCatalogueItems() {
        try {
            postCreateCatalogueItem(prepareCatalogueItem(prepareRandomSKUNumber()));
            postCreateCatalogueItem(prepareCatalogueItem(prepareRandomSKUNumber()));

            given()
                .get("/")
            .then()
                .assertThat().spec(prepareResponseSpec(200))
            .and()
                .assertThat().body("data", is(not(empty())));
        }
        catch (Exception e) {
            fail("Error occurred while testing fetch catalogue items", e);
        }
    }

    @Test
    @DisplayName("Test Get Catalogue Item")
    public void test_getCatalogueItem() {
        try {
            // Create Catalogue Item
            CatalogueItem catalogueItem = prepareCatalogueItem(prepareRandomSKUNumber());
            postCreateCatalogueItem(catalogueItem);

            // Get Catalogue item with the sku of the catalogue item that is created and compare the response fields
            given()
                .pathParam("sku", catalogueItem.getSku())
            .when()
                .get("/{sku}")
            .then()
                .assertThat().spec(prepareResponseSpec(200))
            .and()
                .assertThat().body("name", equalTo(catalogueItem.getName()))
            .and()
                .assertThat().body("category", equalTo(catalogueItem.getCategory()));
        }
        catch(Exception e) {
            fail("Error occurred while testing fetch catalogue item", e);
        }
    }

    @Test
    @DisplayName("Test Update Catalogue Item")
    public void test_updateCatalogueItem() {
        try {
            // Create Catalogue Item
            CatalogueItem catalogueItem = prepareCatalogueItem(prepareRandomSKUNumber());
            postCreateCatalogueItem(catalogueItem);

            // Update catalogue item
            catalogueItem.setName("Updated-"+catalogueItem.getName());
            catalogueItem.setDescription("Updated-"+catalogueItem.getDescription());

            given()
                .contentType("application/json")
                .body(catalogueItem)
                .pathParam("sku", catalogueItem.getSku())
            .when()
                .put("/{sku}")
            .then()
                .assertThat().spec(prepareResponseSpec(200));

            // Get updated catalogue item with the sku of the catalogue item that is created and compare the response fields
            given()
                .pathParam("sku", catalogueItem.getSku())
            .when()
                .get("/{sku}")
            .then()
                .assertThat().spec(prepareResponseSpec(200))
            .and()
                .assertThat().body("name", equalTo(catalogueItem.getName()))
            .and()
                .assertThat().body("category", equalTo(catalogueItem.getCategory()));
        }
        catch(Exception e) {
            fail("Error occurred while testing catalogue item update", e);
        }
    }

    @Test
    @DisplayName("Test Delete Catalogue Item")
    public void test_deleteCatalogueItem() {
        try {
            // Create Catalogue Item
            CatalogueItem catalogueItem = prepareCatalogueItem(prepareRandomSKUNumber());
            postCreateCatalogueItem(catalogueItem);

            // Delete Catalogue Item
            given()
                .pathParam("sku", catalogueItem.getSku())
            .when()
                .delete("/{sku}")
            .then()
                .assertThat().spec(prepareResponseSpec(204));

            // Trying to get the deleted catalogue item should throw 400
            given()
                .pathParam("sku", catalogueItem.getSku())
            .when()
                .get("/{sku}")
            .then()
                .assertThat().spec(prepareResponseSpec(404));
        }
        catch(Exception e) {
            fail("Error occurred while testing catalogue item update", e);
        }
    }

    @Test
    @DisplayName("Test Resource not found")
    public void test_resourceNotFound() {
        try {
            given()
                .pathParam("sku", prepareRandomSKUNumber())
                .get("/{sku}")
            .then()
                .assertThat().spec(prepareResponseSpec(404));
        }
        catch(Exception e) {
            fail("Error occurred while testing resource not found", e);
        }
    }

    @Test
    @DisplayName("Test validation error")
    public void test_validationErrors() {
        try {
            CatalogueItem catalogueItem = prepareCatalogueItem(prepareRandomSKUNumber());
            catalogueItem.setCategory("INVALID");

            Response response
                = postCreateCatalogueItem(catalogueItem)
                .then()
                    .assertThat().spec(prepareResponseSpec(400))
                .and()
                    .extract().response();

            List<Error> errors = Arrays.asList(response.getBody().jsonPath().getObject("errors", Error[].class));

            assertTrue(errors != null && errors.size() > 0);

            assertTrue(errors.get(0).getDescription().equalsIgnoreCase("Invalid category provided"));
        }
        catch(Exception e) {
            fail("Error occurred while testing validation errors", e);
        }
    }

    @Test
    @DisplayName("Test Invalid Request")
    public void test_invalidRequest() {
        try {
            // Create Catalogue Item via JsonObject
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();
            ((ObjectNode) rootNode).put("name", "INVALID");
            ((ObjectNode) rootNode).put("sku", prepareRandomSKUNumber());
            ((ObjectNode) rootNode).put("price", "INVALID");

            String catalogueItem = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);

            Response response =
                given()
                    .contentType("application/json")
                    .body(catalogueItem)
                    .post("/")
                .then()
                    .assertThat().spec(prepareResponseSpec(400))
                .and()
                    .extract().response();

            List<Error> errors = Arrays.asList(response.getBody().jsonPath().getObject("errors", Error[].class));

            assertTrue(errors != null && errors.size() > 0);

            assertTrue(errors.get(0).getMessage().contains("Invalid request"));
        }
        catch(Exception e) {
            fail("Error occurred while testing invalid request", e);
        }
    }

    private ResponseSpecification prepareResponseSpec(int responseStatus) {
        return new ResponseSpecBuilder()
            .expectStatusCode(responseStatus)
            .build();
    }

    private Response postCreateCatalogueItem(CatalogueItem catalogueItem) throws Exception {
        RequestSpecification request
            = given()
                .contentType("application/json")
                .body(catalogueItem);

        return request.post("/");
    }

    final Random random = new Random();
    private String prepareRandomSKUNumber() {
        return "SKUNUMBER-"+
            random.ints(1000, 9999)
                .findFirst()
                .getAsInt();
    }

    private CatalogueItem prepareCatalogueItem(String skuNumber) {
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