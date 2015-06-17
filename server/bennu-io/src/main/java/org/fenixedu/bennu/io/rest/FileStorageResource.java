package org.fenixedu.bennu.io.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.io.domain.DomainStorage;
import org.fenixedu.bennu.io.domain.FileStorage;
import org.fenixedu.bennu.io.domain.FileSupport;
import org.fenixedu.bennu.io.domain.GenericFile;
import org.fenixedu.bennu.io.domain.LocalFileSystemStorage;

import pt.ist.fenixframework.Atomic;

import com.google.gson.JsonObject;

@Path("/bennu-io/storage")
public class FileStorageResource extends BennuRestResource {

    @POST
    @Path("/default/{storage}")
    public String setDefault(@PathParam("storage") String storageId) {
        accessControl(Group.managers());
        innerSetDefault(this.<FileStorage> readDomainObject(storageId));
        return all();
    }

    @Atomic
    private void innerSetDefault(FileStorage storage) {
        FileSupport.getInstance().setDefaultStorage(storage);
    }

    @POST
    @Path("/domain/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public String createDomainStorage(@PathParam("name") String name) {
        accessControl(Group.managers());
        return view(createDomainStorageService(name));
    }

    @POST
    @Path("/lfs")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String createLFSStorage(String json) {
        accessControl(Group.managers());
        return view(create(json, LocalFileSystemStorage.class));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String all() {
        accessControl(Group.managers());
        return view(FileSupport.getInstance().getFileStorageSet(), "storages");
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    public String fileCount() {
        accessControl(Group.managers());
        JsonObject json = new JsonObject();
        for (FileStorage store : FileSupport.getInstance().getFileStorageSet()) {
            json.addProperty(store.getExternalId(), store.getFileSet().size());
        }
        return json.toString();
    }

    @Atomic
    private DomainStorage createDomainStorageService(String name) {
        return FileStorage.createNewDomainStorage(name);
    }

    @DELETE
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("oid") String storageOid) {
        accessControl(Group.managers());
        final FileStorage fileStorage = (FileStorage) readDomainObject(storageOid);
        final String response = view(fileStorage);
        Boolean deleteStorage = deleteStorage(fileStorage);
        if (deleteStorage) {
            return Response.ok(response).build();
        }
        return Response.noContent().build();
    }

    @Atomic
    private Boolean deleteStorage(FileStorage fileStorage) {
        return fileStorage.delete();
    }

    @PUT
    @Path("/convert/{oid}")
    public Response convertFileStorage(@PathParam("oid") String fileStorageOid) {
        accessControl(Group.managers());
        GenericFile.convertFileStorages((FileStorage) readDomainObject(fileStorageOid));
        return Response.ok().build();
    }

}
