package com.toomuch2learn.crud.catalogue.event;

import com.toomuch2learn.crud.catalogue.service.CatalogueCrudService;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.*;

@ApplicationScoped
public class ProductPurchasedReceivedEvent {

    private Logger log = LoggerFactory.getLogger(ProductPurchasedReceivedEvent.class);

    @Inject
    CatalogueCrudService catalogueCrudService;

    ExecutorService executor;
    private BlockingQueue<String> messages;

    @PostConstruct
    public void handlePurchasedProduct() {
        messages = new LinkedBlockingQueue<>();

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
        executor.scheduleAtFixedRate(() -> {
            if (messages.size() > 0) {
                log.error("==> purchased products available");
                try {
                    catalogueCrudService.productPurchased(messages.take());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 1000, 2000, TimeUnit.SECONDS);
    }

    @Incoming("product-purchased")
    public void productPurchased(String skuNumber) {
        log.error("===> Purchased product received for skuNumber :: "+skuNumber);
        messages.add(skuNumber);
    }
}
