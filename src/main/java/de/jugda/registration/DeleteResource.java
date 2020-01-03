package de.jugda.registration;

import de.jugda.registration.model.DeregistrationForm;
import de.jugda.registration.service.DeleteService;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Path("delete")
@Produces(MediaType.TEXT_HTML)
public class DeleteResource {

    @Inject
    DeleteService deleteService;
    @Inject
    Template delete;
    @Inject
    Template delete_thanks;

    @GET
    public TemplateInstance getDeleteForm(@QueryParam("eventId") String eventId, @QueryParam("id") String id) {
        if (id == null) {
            return delete.data("eventId", eventId);
        } else {
            String name = deleteService.deleteFromUri(id);
            return delete_thanks.data("name", name);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public TemplateInstance postDeleteForm(@Valid @BeanParam DeregistrationForm deregistrationForm) {
        String name = deleteService.deleteFromUi(deregistrationForm);
        return delete_thanks.data("name", name);
    }

    @DELETE
    public void deleteById(@QueryParam("id") String id) {
        deleteService.delete(id);
    }

}
