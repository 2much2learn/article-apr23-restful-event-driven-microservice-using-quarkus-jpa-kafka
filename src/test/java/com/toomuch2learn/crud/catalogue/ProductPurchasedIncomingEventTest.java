package com.toomuch2learn.crud.catalogue;

import com.toomuch2learn.crud.catalogue.config.KafkaTestResource;
import com.toomuch2learn.crud.catalogue.model.CatalogueItem;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.DisabledOnNativeImage;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTestResource(KafkaTestResource.class)
@QuarkusTest
@DisabledOnNativeImage
public class ProductPurchasedIncomingEventTest extends BaseTest{

    private Logger log = LoggerFactory.getLogger(PriceUpdatedOutgoingEventTest.class);

    public static Producer<String, String> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getProperty("kafka.bootstrap.servers"));
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "product-purchased");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return new KafkaProducer<String, String>(props);
    }

    @Test
    public void testProductPurchasedIncomingEvent() throws Exception {
        RestAssured.baseURI = "http://[::1]:8081/api/v1";
        try {

            String sku = prepareRandomSKUNumber();

            // Create Catalogue Item
            CatalogueItem catalogueItem = prepareCatalogueItem(sku);
            postCreateCatalogueItem(catalogueItem);

            // Wait for 10 seconds before publishing the message for the cluster to startup properly
            Thread.sleep(10000);

            log.info(String.format("===> Producing product purchased event for %s", sku));
            Producer<String, String> producer = createProducer();
            producer.send(new ProducerRecord<>("product-purchased", "testcontainers", sku));

            // Wait for 10 seconds for the message to be handled by the application
            Thread.sleep(10000);

            log.info(String.format("===> Invocking get request for ", sku));

            // Get Catalogue item with the sku of the catalogue item that is created and compare the response fields
            Response response = given()
                .pathParam("sku", sku)
            .when()
                .get("/{sku}");

            CatalogueItem getCatalogueItem = response.getBody().as(CatalogueItem.class);
            log.info(String.format("===> Received response for %s with inventory-%s", sku, getCatalogueItem.getInventory()));

            Assertions.assertEquals(getCatalogueItem.getInventory(), catalogueItem.getInventory() - 1);
        }
        catch(Exception e) {
            fail("Error occurred while testing Product Purchased event", e);
        }
    }
}
