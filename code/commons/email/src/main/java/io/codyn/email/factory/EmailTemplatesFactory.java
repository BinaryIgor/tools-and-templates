package io.codyn.email.factory;

import io.codyn.email.EmailTemplatesSource;
import io.codyn.email.EmailTemplatesValidator;
import io.codyn.email.model.EmailTemplates;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class EmailTemplatesFactory {

    public static EmailTemplates validatedTemplates(String templatesDir,
                                                    Map<String, List<String>> typesVariables,
                                                    Collection<String> requiredLanguages) {
        var templates = EmailTemplatesSource.fromFiles(new File(templatesDir, "email"),
                requiredLanguages);
        EmailTemplatesValidator.validate(templates, typesVariables, requiredLanguages);
        return templates;
    }
}
