package com.toomuch2learn.crud.catalogue.controller;

import com.toomuch2learn.crud.catalogue.exception.ResourceNotFoundException;
import com.toomuch2learn.crud.catalogue.model.CatalogueItem;
import com.toomuch2learn.crud.catalogue.model.CatalogueItemList;
import com.toomuch2learn.crud.catalogue.model.ResourceIdentity;
import com.toomuch2learn.crud.catalogue.service.CatalogueCrudService;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(CatalogueControllerAPIPaths.BASE_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class CatalogueController {

    @Inject
    CatalogueCrudService catalogueCrudService;

    @GET
    @Path(CatalogueControllerAPIPaths.GET_ITEMS)
    @Counted(name = "countGetCatalogueItems", description = "Counts how many times the getCatalogueItems method has been invoked")
    @Timed(name = "timeGetCatalogueItems", description = "Times how long it takes to invoke the getCatalogueItems method", unit = MetricUnits.MILLISECONDS)
    public Response getCatalogueItems() throws Exception {
        return Response.ok(new CatalogueItemList(catalogueCrudService.getCatalogueItems())).build();
    }

    @GET
    @Path(CatalogueControllerAPIPaths.GET_ITEM)
    public Response
        getCatalogueItemBySKU(@PathParam(value = "sku") String skuNumber)
            throws ResourceNotFoundException, Exception {

        return Response.ok(catalogueCrudService.getCatalogueItem(skuNumber)).build();
    }

    @POST
    @Path(CatalogueControllerAPIPaths.CREATE)
    public Response addCatalogueItem(@Valid CatalogueItem catalogueItem) throws Exception{

        Long id = catalogueCrudService.addCatalogItem(catalogueItem);

        return Response.status(Response.Status.CREATED).entity(new ResourceIdentity(id)).build() ;
    }

    @PUT
    @Path(CatalogueControllerAPIPaths.UPDATE)
    public Response updateCatalogueItem(
        @PathParam(value = "sku") String skuNumber,
        @Valid CatalogueItem catalogueItem) throws ResourceNotFoundException, Exception {

        catalogueCrudService.updateCatalogueItem(catalogueItem);

        return Response.ok().build();
    }

    @DELETE
    @Path(CatalogueControllerAPIPaths.DELETE)
    public Response removeCatalogItem(@PathParam(value = "sku") String skuNumber) throws ResourceNotFoundException, Exception {

        catalogueCrudService.deleteCatalogueItem(skuNumber);

        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
