package de.jugda.registration;

import de.jugda.registration.model.DeregistrationForm;
import de.jugda.registration.service.DeleteService;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.Set;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Path("delete")
@Produces(MediaType.TEXT_HTML)
public class DeleteResource {

    @Inject
    DeleteService deleteService;
    @Inject
    Validator validator;
    @Inject
    Template delete;
    @Inject
    Template delete_thanks;

    @GET
    public TemplateInstance getDeleteForm(@QueryParam("eventId") String eventId, @QueryParam("id") String id) {
        if (id == null) {
            DeregistrationForm form = new DeregistrationForm();
            form.setEventId(eventId);
            return delete.data(form);
        } else {
            String name = deleteService.deleteFromUri(id);
            return delete_thanks.data("name", name);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public TemplateInstance postDeleteForm(@BeanParam DeregistrationForm deregistrationForm) {
        Set<ConstraintViolation<DeregistrationForm>> violations = validator.validate(deregistrationForm);
        if (violations.isEmpty()) {
            String name = deleteService.deleteFromUi(deregistrationForm);
            return delete_thanks.data("name", name);
        } else {
            violations.forEach(cv ->
                deregistrationForm.addValidationError(cv.getPropertyPath().toString(), cv.getMessage()));
            return delete.data(deregistrationForm);
        }
    }

    @DELETE
    public void deleteById(@QueryParam("id") String id) {
        deleteService.delete(id);
    }

}
