package de.jugda.registration;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import lombok.SneakyThrows;

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
    }

    @SneakyThrows
    public String getRegistrationForm(Map<String, Object> model) {
        Template template = handlebars.compile("registration");
        return template.apply(model);
    }

    @SneakyThrows
    public String getThanksPage(Map<String, Object> model) {
        Template template = handlebars.compile("thanks");
        return template.apply(model);
    }

}
