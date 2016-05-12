package io.exo.cloudstack.restrictions;

import java.io.IOException;
import java.util.List;

import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.cloud.exception.InvalidParameterValueException;
import com.cloud.utils.PropertiesUtil;

public class RestrictionsManager {
    private static final Logger s_logger = Logger.getLogger(RestrictionsManager.class);

    private static Restrictions restrictions = null;

    public RestrictionsManager() {
        if (restrictions == null) {
            restrictions = loadRestrictions();
        }
    }

    private Restrictions loadRestrictions() {

        Restrictions restrictions = null;
        try {
            final Path path = PropertiesUtil.findConfigFile("restrictions.yaml").toPath();
            final byte[] ba = Files.readAllBytes(path);
            final String data = new String(ba, "UTF-8");

            Constructor ctor = new Constructor(Restrictions.class);
            Yaml parser = new Yaml(ctor);

            restrictions = (Restrictions) parser.load(data);
            if (s_logger.isDebugEnabled()) {
                s_logger.debug("Loaded " + restrictions.size() + " restrictions");
                for (Restriction res : restrictions.getRestrictions()) {
                    s_logger.debug(res);
                }
            }
        } catch (IOException e) {
            s_logger.error("Could not load restrictions yaml file", e);
        }
        return restrictions;
    }

    public void reloadRestrictions() {
        Restrictions newRestrictions = loadRestrictions();
        if (newRestrictions != null) {
            restrictions = newRestrictions;
        }
    }

    public void enforceRestrictions(String serviceOfferingName, String templateName, Long templateSize) throws InvalidParameterValueException {

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

    public List<Restriction> getRestrictions() throws IOException {
        return restrictions.getRestrictions();
    }
}
