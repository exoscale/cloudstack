package io.exo.cloudstack.restrictions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.cloud.exception.InvalidParameterValueException;
import com.cloud.utils.PropertiesUtil;

public class RestrictionServiceImpl implements RestrictionService {
    private static final Logger s_logger = Logger.getLogger(RestrictionServiceImpl.class);

    // Working directly with the list instead of the RestrictionList object
    private static List<Restriction> restrictions = null;

    public RestrictionServiceImpl() {
        if (restrictions == null) {
            restrictions = loadRestrictions();
        }
    }

    private List<Restriction> loadRestrictions() {

        List<Restriction> restrictionsList = null;
        try {
            final File file = PropertiesUtil.findConfigFile("restrictions.yaml");
            final Path path = file.toPath();
            final byte[] ba = Files.readAllBytes(path);
            final String data = new String(ba, "UTF-8");

            Constructor ctor = new Constructor(RestrictionList.class);
            Yaml parser = new Yaml(ctor);

            RestrictionList restrictionList = (RestrictionList) parser.load(data);
            if (s_logger.isDebugEnabled()) {
                s_logger.debug("Loaded " + restrictionList.size() + " restrictionList");
                for (Restriction res : restrictionList.getRestrictions()) {
                    s_logger.debug(res);
                }
            }
            restrictionsList = restrictionList.getRestrictions();
        } catch (Exception e) {
            s_logger.error("Could not load restrictionList yaml file", e);
        }
        return restrictionsList;
    }

    @Override
    public void reloadRestrictions() {
        List<Restriction> newRestrictions = loadRestrictions();
        if (newRestrictions != null) {
            restrictions = newRestrictions;
        }
    }

    @Override
    public void validate(String serviceOfferingName, String templateName, Long templateSize) throws InvalidParameterValueException {

        s_logger.debug("Enforce restrictions on serviceOfferingName=" + (serviceOfferingName == null ? "null" : serviceOfferingName) + ", templateName=" + (templateName == null ? "null" : templateName) + ", templateSize=" + (templateSize == null ? "null" : templateSize));

        if (serviceOfferingName == null) {
            return;
        }

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


    }

    public List<Restriction> getRestrictions() {
        if (restrictions != null) {
            return restrictions;
        }
        return new ArrayList<>(0);
    }

}
