package de.jugda.registration.service;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Log4j
public class HandlebarsService {

    private final Handlebars handlebars;

    public HandlebarsService() {
        TemplateLoader loader = new ClassPathTemplateLoader();
        loader.setPrefix("/templates");
        loader.setSuffix(".html");

        handlebars = new Handlebars(loader);
        registerHandlers();
    }

    public String getRegistrationForm(Map<String, Object> model) {
        return renderTemplate("registration", model);
    }

    public String getThanksPage(Map<String, Object> model) {
        return renderTemplate("thanks", model);
    }

    public String getRegistrationNotYetOpen(Map<String, Object> model) {
        return renderTemplate("not_yet_open", model);
    }

    public String getRegistrationClosed() {
        return renderTemplate("closed", Collections.emptyMap());
    }

    public String getRegistrationsList(Map<String, Object> model) {
        return renderTemplate("list", model);
    }

    public String getRegistrationsOverview(Map<String, Object> model) {
        return renderTemplate("overview", model);
    }

    public String getDeregistration(Map<String, Object> model) {
        return renderTemplate("delete", model);
    }

    public String getDeregistrationThanks(Map<String, Object> model) {
        return renderTemplate("delete_thanks", model);
    }

    public String getRegistrationConfirmMail(Map<String, Object> model) {
        return renderTemplate("mail_registration", model);
    }

    public String getWaitlistToAttendeeMail(Map<String, Object> model) {
        return renderTemplate("mail_waitlist2attendee", model);
    }

    private String renderTemplate(String templateName, Map<String, ?> model) {
        try {
            Template template = handlebars.compile(templateName);
            return template.apply(model);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new UncheckedIOException(e);
        }
    }

    private void registerHandlers() {
        handlebars.registerHelper("datetime", (Helper<Date>)
            (date, options) -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
    }

}
