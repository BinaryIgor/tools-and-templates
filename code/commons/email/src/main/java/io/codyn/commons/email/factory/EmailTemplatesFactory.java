package io.codyn.commons.email.factory;

import io.codyn.commons.email.EmailTemplatesSource;
import io.codyn.commons.email.EmailTemplatesValidator;
import io.codyn.commons.email.model.EmailTemplates;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class EmailTemplatesFactory {

    public static EmailTemplates validatedTemplates(String templatesDir,
                                                    Map<String, List<String>> typesVariables,
                                                    Collection<String> requiredLanguages) {
        var templates = EmailTemplatesSource.fromFiles(new File(templatesDir, "email"),
                typesVariables.keySet());
        EmailTemplatesValidator.validate(templates, typesVariables, requiredLanguages);
        return templates;
    }
}
