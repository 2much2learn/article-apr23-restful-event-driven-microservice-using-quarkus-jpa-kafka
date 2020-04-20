package com.toomuch2learn.crud.catalogue;

import com.toomuch2learn.crud.catalogue.config.KafkaTestResource;
import com.toomuch2learn.crud.catalogue.model.CatalogueItem;
import com.toomuch2learn.crud.catalogue.model.ProductPrice;
import com.toomuch2learn.crud.catalogue.serializer.ProductPriceDeserializer;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.DisabledOnNativeImage;
import io.quarkus.test.junit.QuarkusTest;

import io.restassured.RestAssured;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTestResource(KafkaTestResource.class)
@QuarkusTest
@DisabledOnNativeImage
public class PriceUpdatedOutgoingEventTest extends BaseTest{

    private Logger log = LoggerFactory.getLogger(PriceUpdatedOutgoingEventTest.class);

    public static KafkaConsumer<Integer, ProductPrice> createConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getProperty("kafka.bootstrap.servers"));
        props.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, "true");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "price-updated");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ProductPriceDeserializer.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<Integer, ProductPrice> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("price-updated"));

        return consumer;
    }

    @Test
    public void test() throws Exception {

        RestAssured.baseURI = "http://[::1]:8081/api/v1";

        try {
            KafkaConsumer<Integer, ProductPrice> consumer = createConsumer();

            String sku = prepareRandomSKUNumber();
            // Create Catalogue Item
            CatalogueItem catalogueItem = prepareCatalogueItem(sku);
            postCreateCatalogueItem(catalogueItem);

            // Update catalogue item
            double priceUpdatedTo = 99.99;
            catalogueItem.setPrice(priceUpdatedTo);

            given()
                .contentType("application/json")
                .body(catalogueItem)
                .pathParam("sku", sku)
            .when()
                .put("/{sku}")
            .then()
                .assertThat().spec(prepareResponseSpec(200));

            Unreliables.retryUntilTrue(45, TimeUnit.SECONDS, () -> {
                ConsumerRecords<Integer, ProductPrice> records = consumer.poll(Duration.ofMillis(100));

                if (records.isEmpty()) {
                    return false;
                }

                records.forEach(record -> {
                    log.info(String.format("==> Received %s ", record.value().getSkuNumber()));
                    if(record.value().getSkuNumber().equals(sku)) {
                        log.info(String.format("==> Product price received :: %s - %s", record.value().getSkuNumber(), record.value().getPrice()));
                        Assertions.assertEquals(record.value().getPrice(), priceUpdatedTo);
                    }
                });

                return true;
            });
            consumer.unsubscribe();
        }
        catch(Exception e) {
            fail("Error occurred while testing price updated event", e);
        }
    }
}
