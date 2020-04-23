package com.toomuch2learn.crud.catalogue.event;

import com.toomuch2learn.crud.catalogue.service.CatalogueCrudService;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.concurrent.*;

@ApplicationScoped
public class ProductPurchasedReceivedEvent {

    private Logger log = LoggerFactory.getLogger(ProductPurchasedReceivedEvent.class);

    @Inject
    CatalogueCrudService catalogueCrudService;

    private ExecutorService executor;
    private BlockingQueue<String> messages;

    void startup(@Observes StartupEvent event) {
        log.info("========> ProductPurchasedReceivedEvent startup");
        messages = new LinkedBlockingQueue<>();

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
        executor.scheduleAtFixedRate(() -> {
            if (messages.size() > 0) {
                log.error("====> purchased products available");
                try {
                    catalogueCrudService.productPurchased(messages.take());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 1000, 2000, TimeUnit.MILLISECONDS);
    }

    @Incoming("product-purchased")
    public void productPurchased(String skuNumber) {
        log.error("=====> Purchased product received for skuNumber :: "+skuNumber);
        messages.add(skuNumber);
    }
}