package io.exo.cloudstack.restrictions;

import java.io.IOException;
import java.util.List;

import java.nio.file.Files;
import java.nio.file.Path;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.constructor.Constructor;

import com.cloud.exception.InvalidParameterValueException;
import com.cloud.utils.PropertiesUtil;

public class RestrictionListManager {

    private static RestrictionList restrictionList = null;
    private static Boolean loaded = false;

    private static void loadFromYaml(String data) {

        Constructor ctor = new Constructor(RestrictionList.class);
        TypeDescription tdesc = new TypeDescription(RestrictionList.class);

        tdesc.putMapPropertyType("restrictions", Restriction.class, Object.class);
        ctor.addTypeDescription(tdesc);
        Yaml parser = new Yaml(ctor);

        restrictionList = (RestrictionList) parser.load(data);
    }

    private static void loadRestrictionList() throws IOException {

        final Path path = PropertiesUtil.findConfigFile("restrictions.yaml").toPath();
        final byte[] ba = Files.readAllBytes(path);
        final String data = new String(ba, "UTF-8");

        loadFromYaml(data);
        loaded = true;
    }

    public static void enforceRestrictions(String serviceOfferingName, String templateName, Long templateSize) throws InvalidParameterValueException {

        /*
         * Do not blow up when given invalid input.
         */
        if (serviceOfferingName == null)
            return;

        try {
            final List<Restriction> restrictions = getRestrictions();

            for (Restriction restriction: restrictions) {

                if (restriction.getTemplateNamePattern() != null && templateName != null) {
                    if (serviceOfferingName.equals(restriction.getServiceOfferingName()) &&
                        restriction.getTemplateNamePattern().matcher(templateName).find()) {
                        throw new InvalidParameterValueException("Template is restricted for this service offering.");
                    }
                }

                if (restriction.getMaxTemplateSize() != null) {
                    if (serviceOfferingName.equals(restriction.getServiceOfferingName()) &&
                        (restriction.getMaxTemplateSize() < templateSize)) {
                        throw new InvalidParameterValueException("The required disk size is restricted for this template");
                    }
                }
            }

        } catch (IOException e) {
            /* we could not load restrictions, we should log but not interrupt execution */
        }

    }

    public static List<Restriction> getRestrictions() throws IOException {

        if (!loaded) /* Only read once at startup */
            loadRestrictionList();

        return restrictionList.getRestrictions();
    }
}
