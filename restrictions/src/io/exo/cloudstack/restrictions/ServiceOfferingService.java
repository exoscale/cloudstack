package io.exo.cloudstack.restrictions;

import com.cloud.exception.InvalidParameterValueException;
import com.cloud.offering.ServiceOffering;

public interface ServiceOfferingService {
    boolean isAuthorized(ServiceOffering serviceOffering, Long domainId, Long accountId);
    void reloadStaticRestrictions();
    void validate(String serviceOfferingName, String templateName, Long templateSize) throws InvalidParameterValueException;
}
