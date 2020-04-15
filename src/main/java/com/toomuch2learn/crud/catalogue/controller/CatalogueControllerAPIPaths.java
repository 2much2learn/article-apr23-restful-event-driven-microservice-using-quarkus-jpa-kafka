package com.toomuch2learn.crud.catalogue.controller;

public class CatalogueControllerAPIPaths {

    public static final String BASE_PATH = "/api/v1";

    public static final String CREATE = "/";
    public static final String GET_ITEMS = "/";
    public static final String GET_ITEM = "/{sku}";
    public static final String UPDATE = "/{sku}";
    public static final String DELETE = "/{sku}";
    public static final String UPLOAD_IMAGE = "/{sku}/image";
}
