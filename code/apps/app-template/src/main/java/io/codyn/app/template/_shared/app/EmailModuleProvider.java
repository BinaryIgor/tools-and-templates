package io.codyn.app.template._shared.app;


import io.codyn.app.template._shared.domain.email.Emails;
import io.codyn.app.template._shared.domain.model.ApplicationLanguage;
import io.codyn.commons.email.factory.EmailFactory;
import io.codyn.commons.email.factory.EmailTemplatesFactory;
import io.codyn.commons.email.factory.TemplatesEmailFactory;
import io.codyn.commons.email.model.EmailTemplates;
import io.codyn.tools.FilePathFinder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class EmailModuleProvider {

    public static EmailFactory factory(String templatesDirectory) {
        if (templatesDirectory == null || !Files.exists(Path.of(templatesDirectory))) {
            templatesDirectory = FilePathFinder.templatesUpFromCurrentPath();
        }
        return new TemplatesEmailFactory(templates(templatesDirectory));
    }

    public static EmailTemplates templates(String templatesDirectory) {
        return EmailTemplatesFactory.validatedTemplates(templatesDirectory, Emails.TYPES_VARIABLES,
                Arrays.stream(ApplicationLanguage.values()).map(Enum::name).toList());
    }
}
