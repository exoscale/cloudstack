package io.exo.cloudstack.restrictions;

import com.cloud.exception.InvalidParameterValueException;

public interface RestrictionService {

    void reloadRestrictions();
    void validate(String serviceOfferingName, String accountUuid, String templateName, Long templateSize) throws InvalidParameterValueException;
}
