package pt.ist.bennu.portal.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import pt.ist.bennu.core.rest.BennuRestResource;
import pt.ist.bennu.portal.domain.MenuItem;
import pt.ist.fenixframework.Atomic;

@Path("menu")
public class MenuResource extends BennuRestResource {

    @Path("{oid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getMenu(@PathParam("oid") final String menuOid) {
        accessControl("#managers");
        return viewMenu((MenuItem) readDomainObject(menuOid));
    }

    private String viewMenu(MenuItem menuItem) {
        return view(menuItem);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String create(final String jsonData) {
        accessControl("#managers");
        return innerCreate(jsonData);
    }

    @Atomic
    public String innerCreate(final String jsonData) {
        return view(create(jsonData, MenuItem.class));
    }

    @Path("{oid}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String update(final String jsonData, @PathParam("oid") final String oid) {
        accessControl("#managers");
        return view(innerUpdate((MenuItem) readDomainObject(oid), jsonData));
    }

    @Atomic
    public MenuItem innerUpdate(final MenuItem menuItem, final String jsonData) {
        return update(jsonData, menuItem);
    }

    @Path("{oid}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteMenu(@PathParam("oid") final String menuOid) {
        accessControl("#managers");
        final MenuItem menuItem = (MenuItem) readDomainObject(menuOid);
        final String rsp = viewMenu(menuItem);
        menuItem.delete();
        return rsp;
    }
}
