package io.exo.cloudstack.restrictions;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import com.cloud.offering.ServiceOffering;
import com.cloud.service.dao.ServiceOfferingAuthorizationDao;
import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.cloud.exception.InvalidParameterValueException;
import com.cloud.utils.PropertiesUtil;

import javax.inject.Inject;

public class ServiceOfferingServiceImpl implements ServiceOfferingService {
    private static final Logger s_logger = Logger.getLogger(ServiceOfferingServiceImpl.class);

    private static final String DEFAULTFILENAME = "restrictions.yaml";

    @Inject
    ServiceOfferingAuthorizationDao serviceOfferingAuthorizationDao;

    // Working directly with the map of restrictions
    private volatile Map<String, List<Restriction>> restrictions = null;

    public ServiceOfferingServiceImpl() {
        this(DEFAULTFILENAME);
    }

    public ServiceOfferingServiceImpl(String filename) {
        restrictions = loadRestrictions(filename);
    }

    private static Map<String, List<Restriction>> loadRestrictions(String filename) {

        Map<String, List<Restriction>> restrictionsMap = null;
        try {
            final File file = PropertiesUtil.findConfigFile(filename);
            final Path path = file.toPath();
            final byte[] ba = Files.readAllBytes(path);
            final String data = new String(ba, "UTF-8");

            Constructor ctor = new Constructor(RestrictionList.class);
            Yaml parser = new Yaml(ctor);

            RestrictionList restrictionList = (RestrictionList) parser.load(data);
            if (s_logger.isDebugEnabled()) {
                s_logger.debug("Loaded " + restrictionList.size() + " restrictions");
                for (Restriction res : restrictionList.getRestrictions()) {
                    s_logger.debug(res);
                }
            }
            restrictionsMap = restrictionList.getRestrictionsMap();
        } catch (Exception e) {
            s_logger.error("Could not load restrictions yaml file", e);
        }
        return restrictionsMap;
    }

    @Override
    public boolean isAuthorized(ServiceOffering serviceOffering, Long domainId, Long accountId) {
        if (serviceOffering.isRestricted()) {
            final int count = serviceOfferingAuthorizationDao.count(serviceOffering.getId(), domainId, accountId);
            s_logger.debug("Found " + count + " authorization matching for service offering " + serviceOffering.getName() + " on domainId=" + (domainId == null ? "null" : domainId) + ", accountId=" + (accountId == null ? "null" : accountId));
            if (count > 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public void reloadStaticRestrictions() {
        s_logger.debug("Reloading restrictions file");
        Map<String, List<Restriction>> newRestrictions = loadRestrictions(DEFAULTFILENAME);
        if (newRestrictions != null) {
            restrictions = newRestrictions;
        }
    }

    @Override
    public void validate(String serviceOfferingName, String templateName, Long templateSize) throws InvalidParameterValueException {

        s_logger.debug("Enforce restrictions on serviceOfferingName=" + (serviceOfferingName == null ? "null" : serviceOfferingName) + ", templateName=" + (templateName == null ? "null" : templateName) + ", templateSize=" + (templateSize == null ? "null" : templateSize));

        if (serviceOfferingName == null) {
            s_logger.error("Missing service offering in restriction call");
            return;
        }

        if (restrictions != null && restrictions.containsKey(serviceOfferingName)) {
            for (Restriction restriction: restrictions.get(serviceOfferingName)) {
                if (restriction.getTemplateNamePattern() != null && templateName != null && restriction.getTemplateNamePattern().matcher(templateName).find()) {
                    throw new InvalidParameterValueException("This service offering cannot be used with this template.");
                }

                if (restriction.getMaxTemplateSize() != null && templateSize != null && templateSize > restriction.getMaxTemplateSize() ) {
                    throw new InvalidParameterValueException("This service offering cannot be used with this disk size.");
                }
            }
        }
    }

    public Map<String, List<Restriction>> getRestrictions() {
        return restrictions == null ? new HashMap<String, List<Restriction>>(0) : restrictions;
    }
}
