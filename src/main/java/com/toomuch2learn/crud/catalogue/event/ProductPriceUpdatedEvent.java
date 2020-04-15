package com.toomuch2learn.crud.catalogue.event;

import com.toomuch2learn.crud.catalogue.model.ProductPrice;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.LinkedBlockingQueue;

@ApplicationScoped
public class ProductPriceUpdatedEvent {

    private Logger log = LoggerFactory.getLogger(ProductPriceUpdatedEvent.class);

    private BlockingQueue<ProductPrice> messages = new LinkedBlockingQueue<>();

    public void add(ProductPrice message) {
        messages.add(message);
    }

    @Outgoing("price-updated")
    public CompletionStage<Message<ProductPrice>> send() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ProductPrice productPrice = messages.take();

                log.error("Publishing event for updated product: " + productPrice);

                return Message.of(productPrice);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
