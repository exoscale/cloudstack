package io.exo.cloudstack.restrictions;

import com.cloud.exception.InvalidParameterValueException;
import com.cloud.offering.ServiceOffering;

public interface RestrictionService {
    boolean isAuthorized(ServiceOffering serviceOffering, Long domainId, Long accountId);
    void reloadRestrictions();
    void validate(String serviceOfferingName, String templateName, Long templateSize) throws InvalidParameterValueException;
}
