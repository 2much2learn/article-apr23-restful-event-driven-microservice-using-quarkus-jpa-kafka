package com.toomuch2learn.crud.catalogue.service;

import com.toomuch2learn.crud.catalogue.event.ProductPriceUpdatedEvent;
import com.toomuch2learn.crud.catalogue.exception.ResourceNotFoundException;
import com.toomuch2learn.crud.catalogue.model.CatalogueItem;
import com.toomuch2learn.crud.catalogue.model.ProductPrice;
import com.toomuch2learn.crud.catalogue.repository.CatalogueRepository;
import io.quarkus.panache.common.Sort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class CatalogueCrudService {

    private Logger log = LoggerFactory.getLogger(CatalogueCrudService.class);

    @Inject
    CatalogueRepository catalogueRepository;

    @Inject
    ProductPriceUpdatedEvent productPriceUpdatedEvent;

    public List<CatalogueItem> getCatalogueItems() {
        Sort sort = Sort.ascending("name");

        return catalogueRepository.listAll(sort);
    }

    public CatalogueItem getCatalogueItem( String skuNumber) throws ResourceNotFoundException {
        return getCatalogueItemBySku(skuNumber);
    }

    @Transactional
    public Long addCatalogItem(CatalogueItem catalogueItem) {
        catalogueItem.setCreatedOn(new Date());
        catalogueRepository.persist(catalogueItem);

        return catalogueItem.getId();
    }

    @Transactional
    public void updateCatalogueItem(CatalogueItem catalogueItem) throws ResourceNotFoundException{

        CatalogueItem catalogueItemfromDB = getCatalogueItemBySku(catalogueItem.getSku());

        boolean priceDifference = catalogueItemfromDB.getPrice() != catalogueItem.getPrice();

        catalogueItemfromDB.setName(catalogueItem.getName());
        catalogueItemfromDB.setDescription(catalogueItem.getDescription());
        catalogueItemfromDB.setPrice(catalogueItem.getPrice());
        catalogueItemfromDB.setInventory(catalogueItem.getInventory());
        catalogueItemfromDB.setUpdatedOn(new Date());

        // Publish item if price change
        if(priceDifference) {
            log.error("===> Price is difference with database");
            productPriceUpdatedEvent.add(new ProductPrice(catalogueItem.getSku(), catalogueItem.getPrice()));
        }

        catalogueRepository.persist(catalogueItemfromDB);
    }

    @Transactional
    public void deleteCatalogueItem(String skuNumber) throws ResourceNotFoundException {
        CatalogueItem catalogueItem = getCatalogueItem(skuNumber);

        catalogueRepository.delete(catalogueItem);

        catalogueRepository.flush();
    }

    private CatalogueItem getCatalogueItemBySku(String skuNumber) throws ResourceNotFoundException {
        CatalogueItem catalogueItem = catalogueRepository.findBySku(skuNumber)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format("Catalogue Item not found for the provided SKU :: %s" , skuNumber)));

        return catalogueItem;
    }

    @Transactional
    public void productPurchased(String skuNumber) {
        try {
            CatalogueItem catalogueItem = getCatalogueItem(skuNumber);

            if(catalogueItem.getInventory() > 0) {
                catalogueItem.setInventory(catalogueItem.getInventory()-1);
                updateCatalogueItem(catalogueItem);
            }
        }
        catch (ResourceNotFoundException e) {
            log.error(String.format("Catalogue Item not found for the provided SKU :: %s" , skuNumber));
        }
    }
}
