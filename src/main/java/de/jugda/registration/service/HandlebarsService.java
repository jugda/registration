package de.jugda.registration.service;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import lombok.SneakyThrows;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
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

    public String getRegistrationFull(Map<String, Object> model) {
        return renderTemplate("full", model);
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

    @SneakyThrows
    private String renderTemplate(String templateName, Map<String, ?> model) {
        Template template = handlebars.compile(templateName);
        return template.apply(model);
    }

    private void registerHandlers() {
        handlebars.registerHelper("datetime", (Helper<Date>)
            (date, options) -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
    }

}
