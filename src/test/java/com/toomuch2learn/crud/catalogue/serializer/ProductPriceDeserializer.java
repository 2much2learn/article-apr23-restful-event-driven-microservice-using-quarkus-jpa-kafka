package com.toomuch2learn.crud.catalogue.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toomuch2learn.crud.catalogue.model.ProductPrice;
import org.apache.kafka.common.serialization.Deserializer;

public class ProductPriceDeserializer implements Deserializer {

    @Override
    public ProductPrice deserialize(String arg0, byte[] arg1) {
        ObjectMapper mapper = new ObjectMapper();
        ProductPrice productPrice = null;
        try {
            productPrice = mapper.readValue(arg1, ProductPrice.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productPrice;
    }
}
