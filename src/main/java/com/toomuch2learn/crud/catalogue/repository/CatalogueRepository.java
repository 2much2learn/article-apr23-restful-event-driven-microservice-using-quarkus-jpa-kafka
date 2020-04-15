package com.toomuch2learn.crud.catalogue.repository;

import com.toomuch2learn.crud.catalogue.model.CatalogueItem;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class CatalogueRepository implements PanacheRepository<CatalogueItem> {

    public Optional<CatalogueItem> findBySku(String sku) {
        return find("sku", sku).singleResultOptional();
    }
}
